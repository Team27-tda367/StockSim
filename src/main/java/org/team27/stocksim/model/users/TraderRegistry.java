package org.team27.stocksim.model.users;

import org.team27.stocksim.dto.*;
import org.team27.stocksim.model.portfolio.Portfolio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.team27.stocksim.model.users.bot.IBotStrategy;
import org.team27.stocksim.model.users.bot.RandomStrategy;

/**
 * Central registry for managing all traders (users and bots) in the simulation.
 *
 * <p>TraderRegistry implements the Registry pattern to provide unified management
 * of all trading participants. It uses factories for trader creation, prevents
 * duplicate IDs, and maintains a reference to the current user for UI purposes.</p>
 *
 * <p><strong>Design Patterns:</strong> Registry + Factory + Repository</p>
 * <ul>
 *   <li>Centralized trader storage and retrieval</li>
 *   <li>Delegates creation to ITraderFactory implementations</li>
 *   <li>Prevents duplicate trader IDs (case-insensitive)</li>
 *   <li>Separates users and bots with filtered retrieval</li>
 *   <li>Tracks current user for UI context</li>
 *   <li>Provides DTO conversion for view layer</li>
 * </ul>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Case-insensitive ID handling (auto-uppercase)</li>
 *   <li>Separate factories for users and bots</li>
 *   <li>Configurable starting balances</li>
 *   <li>Current user tracking for UI</li>
 *   <li>Type-safe filtered retrieval</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * ITraderFactory userFactory = new UserFactory();
 * ITraderFactory botFactory = new BotFactory();
 * TraderRegistry registry = new TraderRegistry(userFactory, botFactory);
 *
 * // Create traders
 * registry.createUser("user1", "John Doe", 10000);
 * registry.createBot("bot1", "Trading Bot", new MomentumTraderStrategy());
 *
 * // Set current user for UI
 * registry.setCurrentUser("user1");
 * UserDTO currentUserDto = registry.getCurrentUserDto();
 *
 * // Retrieve traders
 * HashMap<String, User> users = registry.getUsers();
 * HashMap<String, Bot> bots = registry.getBots();
 * Trader trader = registry.getTrader("user1");
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see ITraderRegistry
 * @see Trader
 * @see User
 * @see Bot
 * @see ITraderFactory
 */
public class TraderRegistry implements ITraderRegistry {

    /**
     * Map of all traders keyed by ID (uppercase).
     */
    private final HashMap<String, Trader> traders;

    /**
     * Factory for creating User instances.
     */
    private final ITraderFactory userFactory;

    /**
     * Factory for creating Bot instances.
     */
    private final ITraderFactory botFactory;

    /**
     * Factory function for creating portfolios with specified balance.
     */
    private final Function<Integer, Portfolio> portfolioFactory;

    /**
     * Currently active user for UI context.
     */
    private User currentUser;

    /**
     * Constructs a TraderRegistry with the specified factories.
     *
     * @param userFactory Factory for creating users
     * @param botFactory Factory for creating bots
     */
    public TraderRegistry(ITraderFactory userFactory, ITraderFactory botFactory) {
        this.traders = new HashMap<>();
        this.userFactory = userFactory;
        this.botFactory = botFactory;
        this.portfolioFactory = this::createDefaultPortfolio;
    }

    /**
     * Gets the current user as a DTO with portfolio and order history.
     *
     * @return UserDTO for the current user
     */
    @Override
    public UserDTO getCurrentUserDto() {
        PortfolioDTO userPortfolioDTO = PortfolioMapper.toDto(currentUser.getPortfolio());
        OrderHistoryDTO userOrderHistoryDTO = OrderHistoryMapper.toDto(currentUser.getOrderHistory());
        UserDTO userDTO = UserMapper.toDto(currentUser, userPortfolioDTO, userOrderHistoryDTO);

        return userDTO;
    }

    /**
     * Creates a new user with default balance of 10,000.
     *
     * @param id Unique user identifier
     * @param name Display name
     * @return true if created successfully, false if ID already exists
     */
    @Override
    public boolean createUser(String id, String name) {
        return createUser(id, name, 10000);
    }

    /**
     * Creates a new user with specified starting balance.
     *
     * <p>ID is converted to uppercase. Returns false if a trader
     * with this ID already exists.</p>
     *
     * @param id Unique user identifier
     * @param name Display name
     * @param balance Starting cash balance
     * @return true if created successfully, false if ID already exists
     */
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

    /**
     * Creates a new bot with default RandomStrategy and 10,000 balance.
     *
     * @param id Unique bot identifier
     * @param name Display name
     * @return true if created successfully, false if ID already exists
     */
    @Override
    public boolean createBot(String id, String name) {
        return createBot(id, name, new RandomStrategy());
    }

    /**
     * Creates a new bot with specified strategy and default 10,000 balance.
     *
     * @param id Unique bot identifier
     * @param name Display name
     * @param strategy Trading strategy for the bot
     * @return true if created successfully, false if ID already exists
     */
    @Override
    public boolean createBot(String id, String name, IBotStrategy strategy) {
        return createBot(id, name, strategy, 10000);
    }

    /**
     * Creates a new bot with specified strategy and starting balance.
     *
     * <p>ID is converted to uppercase. Returns false if a trader
     * with this ID already exists.</p>
     *
     * @param id Unique bot identifier
     * @param name Display name
     * @param strategy Trading strategy
     * @param startingBalance Starting cash balance
     * @return true if created successfully, false if ID already exists
     */
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

    /**
     * Checks if a trader ID already exists.
     *
     * @param id The ID to check
     * @return true if ID exists, false otherwise
     */
    private boolean checkDuplicateId(String id) {
        return traders.containsKey(id);
    }

    /**
     * Gets all traders (users and bots).
     *
     * @return HashMap of all traders keyed by ID
     */
    @Override
    public HashMap<String, Trader> getAllTraders() {
        return traders;
    }

    /**
     * Gets only bot traders.
     *
     * @return HashMap of bots keyed by ID
     */
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

    /**
     * Gets only user traders.
     *
     * @return HashMap of users keyed by ID
     */
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

    /**
     * Retrieves a specific trader by ID.
     *
     * <p>Lookup is case-insensitive.</p>
     *
     * @param id The trader ID
     * @return The trader, or null if not found
     */
    @Override
    public Trader getTrader(String id) {
        return traders.get(id.toUpperCase());
    }

    /**
     * Gets the currently selected user for UI context.
     *
     * @return The current user
     */
    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user for UI context.
     *
     * <p>Prints error if user not found.</p>
     *
     * @param userId The ID of the user to set as current
     */
    @Override
    public void setCurrentUser(String userId) {
        User user = getUsers().get(userId.toUpperCase());
        if (user != null) {
            this.currentUser = user;
        } else {
            System.err.println("User not found: " + userId);
        }
    }

    /**
     * Creates a portfolio with the specified starting balance.
     *
     * @param startingBalance Starting cash balance
     * @return New Portfolio instance
     */
    private Portfolio createDefaultPortfolio(int startingBalance) {
        BigDecimal startingBalanceDecimal = new BigDecimal(startingBalance);
        return new Portfolio(startingBalanceDecimal);
    }

}
