package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.team27.stocksim.model.util.MoneyUtils.money;


public class TraderRegistry {

    private final HashMap<String, Trader> traders;
    private final TraderFactory userFactory;
    private final TraderFactory botFactory;
    private final Function<String, Portfolio> portfolioFactory;
    private User currentUser;

    public TraderRegistry(TraderFactory userFactory, TraderFactory botFactory) {
        this.traders = new HashMap<>();
        this.userFactory = userFactory;
        this.botFactory = botFactory;
        this.portfolioFactory = this::createDefaultPortfolio;
    }

    public boolean createUser(String id, String name) {
        String highId = id.toUpperCase();

        if (traders.containsKey(highId)) {
            System.out.println("User ID already exists: " + highId);
            return false;
        }

        Portfolio portfolio = portfolioFactory.apply(highId);
        Trader user = userFactory.createTrader(highId, name, portfolio);
        traders.put(highId, user);
        return true;
    }


    public boolean createBot(String id, String name) {
        String highId = id.toUpperCase();

        if (traders.containsKey(highId)) {
            System.out.println("Bot ID already exists: " + highId);
            return false;
        }

        Portfolio portfolio = portfolioFactory.apply(highId);
        Trader bot = botFactory.createTrader(highId, name, portfolio);
        traders.put(highId, bot);
        return true;
    }


    public HashMap<String, Trader> getAllTraders() {
        return traders;
    }


    public HashMap<String, Trader> getBots() {
        HashMap<String, Trader> bots = new HashMap<>();
        for (Map.Entry<String, Trader> entry : traders.entrySet()) {
            if (entry.getValue() instanceof Bot) {
                bots.put(entry.getKey(), entry.getValue());
            }
        }
        return bots;
    }


    public HashMap<String, User> getUsers() {
        HashMap<String, User> users = new HashMap<>();
        for (Map.Entry<String, Trader> entry : traders.entrySet()) {
            if (entry.getValue() instanceof User) {
                users.put(entry.getKey(), (User) entry.getValue());
            }
        }
        return users;
    }


    public Trader getTrader(String id) {
        return traders.get(id.toUpperCase());
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String userId) {
        User user = getUsers().get(userId.toUpperCase());
        if (user != null) {
            this.currentUser = user;
        } else {
            System.err.println("User not found: " + userId);
        }
    }

    private Portfolio createDefaultPortfolio(String id) {
        BigDecimal startingBalance = money("10000");
        return new Portfolio(startingBalance);
    }
}

