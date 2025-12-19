package org.team27.stocksim.model.users;

import org.team27.stocksim.dto.*;
import org.team27.stocksim.model.portfolio.Portfolio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.team27.stocksim.model.users.bot.IBotStrategy;
import org.team27.stocksim.model.users.bot.RandomStrategy;

public class TraderRegistry implements ITraderRegistry {

    private final HashMap<String, Trader> traders;
    private final ITraderFactory userFactory;
    private final ITraderFactory botFactory;
    private final Function<Integer, Portfolio> portfolioFactory;
    private User currentUser;

    public TraderRegistry(ITraderFactory userFactory, ITraderFactory botFactory) {
        this.traders = new HashMap<>();
        this.userFactory = userFactory;
        this.botFactory = botFactory;
        this.portfolioFactory = this::createDefaultPortfolio;
    }

    @Override
    public UserDTO getCurrentUserDto() {
        PortfolioDTO userPortfolioDTO = PortfolioMapper.toDto(currentUser.getPortfolio());
        OrderHistoryDTO userOrderHistoryDTO = OrderHistoryMapper.toDto(currentUser.getOrderHistory());
        UserDTO userDTO = UserMapper.toDto(currentUser, userPortfolioDTO, userOrderHistoryDTO);

        return userDTO;
    }

    @Override
    public boolean createUser(String id, String name) {
        return createUser(id, name, 10000);
    }

    @Override
    public boolean createUser(String id, String name, int balance) {
        String highId = id.toUpperCase();

        if (checkDuplicateId(highId)) {
            return false;
        }

        Portfolio portfolio = portfolioFactory.apply(balance); // default starting balance
        Trader user = userFactory.createTrader(highId, name, portfolio);
        traders.put(highId, user);
        return true;
    }

    @Override
    public boolean createBot(String id, String name) {
        return createBot(id, name, new RandomStrategy());
    }

    @Override
    public boolean createBot(String id, String name, IBotStrategy strategy) {
        return createBot(id, name, strategy, 10000);
    }

    public boolean createBot(String id, String name, IBotStrategy strategy, int startingBalance) {
        String highId = id.toUpperCase();

        if (checkDuplicateId(highId)) {
            return false;
        }

        Portfolio portfolio = portfolioFactory.apply(startingBalance);
        Trader bot = new Bot(highId, name, portfolio, strategy); // TODO
        traders.put(highId, bot);
        return true;
    }

    private boolean checkDuplicateId(String id) {
        return traders.containsKey(id);
    }

    @Override
    public HashMap<String, Trader> getAllTraders() {
        return traders;
    }

    @Override
    public HashMap<String, Bot> getBots() {
        HashMap<String, Bot> bots = new HashMap<>();
        for (Map.Entry<String, Trader> entry : traders.entrySet()) {
            if (entry.getValue() instanceof Bot) {
                bots.put(entry.getKey(), (Bot) entry.getValue());
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

    private Portfolio createDefaultPortfolio(int startingBalance) {
        BigDecimal startingBalanceDecimal = new BigDecimal(startingBalance);
        return new Portfolio(startingBalanceDecimal);
    }

}
