package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.team27.stocksim.model.users.bot.IBotStrategy;
import static org.team27.stocksim.model.util.MoneyUtils.money;

public class TraderRegistry implements ITraderRegistry {

    private final HashMap<String, Trader> traders;
    private final ITraderFactory userFactory;
    private final ITraderFactory botFactory;
    private final Function<String, Portfolio> portfolioFactory;
    private User currentUser;

    public TraderRegistry(ITraderFactory userFactory, ITraderFactory botFactory) {
        this.traders = new HashMap<>();
        this.userFactory = userFactory;
        this.botFactory = botFactory;
        this.portfolioFactory = this::createDefaultPortfolio;
    }

    @Override
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

    @Override
    public boolean createBot(String id, String name) {
        String highId = id.toUpperCase();
        if (checkDuplicateId(highId)) {
            return false;
        }

        Portfolio portfolio = portfolioFactory.apply(highId);
        Trader bot = botFactory.createTrader(highId, name, portfolio);
        traders.put(highId, bot);
        return true;
    }

    private boolean checkDuplicateId(String id) {
        return traders.containsKey(id);
    }

    @Override
    public boolean createBot(String id, String name, IBotStrategy strategy) {
        String highId = id.toUpperCase();

        if (checkDuplicateId(highId)) {
            return false;
        }

        Portfolio portfolio = portfolioFactory.apply(highId);
        Trader bot = new Bot(highId, name, portfolio, strategy);
        traders.put(highId, bot);
        return true;
    }

    @Override
    public HashMap<String, Trader> getAllTraders() {
        return traders;
    }

    @Override
    public HashMap<String, Trader> getBots() {
        HashMap<String, Trader> bots = new HashMap<>();
        for (Map.Entry<String, Trader> entry : traders.entrySet()) {
            if (entry.getValue() instanceof Bot) {
                bots.put(entry.getKey(), entry.getValue());
            }
        }
        return bots;
    }

    @Override
    public HashMap<String, User> getUsers() {
        HashMap<String, User> users = new HashMap<>();
        for (Map.Entry<String, Trader> entry : traders.entrySet()) {
            if (entry.getValue() instanceof User) {
                users.put(entry.getKey(), (User) entry.getValue());
            }
        }
        return users;
    }

    @Override
    public Trader getTrader(String id) {
        return traders.get(id.toUpperCase());
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
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
