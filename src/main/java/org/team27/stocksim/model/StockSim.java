package org.team27.stocksim.model;

import org.team27.stocksim.dto.InstrumentDTO;
import org.team27.stocksim.dto.StockMapper;
import org.team27.stocksim.dto.UserDTO;
import org.team27.stocksim.model.instruments.IInstrumentRegistry;
import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.instruments.InstrumentRegistry;
import org.team27.stocksim.model.instruments.StockFactory;
import org.team27.stocksim.model.market.IMarket;
import org.team27.stocksim.model.market.Market;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.market.OrderBook;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.simulation.IMarketSimulator;
import org.team27.stocksim.model.simulation.MarketSimulator;
import org.team27.stocksim.model.users.*;
import org.team27.stocksim.observer.IModelObserver;
import org.team27.stocksim.observer.IModelSubject;
import org.team27.stocksim.repository.BotPositionRepository;
import org.team27.stocksim.repository.StockPriceRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Core facade for the stock market simulation system.
 *
 * <p>This class serves as the main entry point and coordinator for the entire stock
 * market simulation. It implements the Facade pattern to provide a simplified interface
 * to the complex subsystems (market, instruments, traders, simulation) and the Observer
 * pattern to notify views of model changes.</p>
 *
 * <p><strong>Design Patterns:</strong> Facade + Observer + Dependency Injection</p>
 * <ul>
 *   <li>Depends on abstractions (IMarket, IInstrumentRegistry, ITraderRegistry) following DIP</li>
 *   <li>Coordinates between market, traders, instruments, and simulation subsystems</li>
 *   <li>Manages observers for MVC architecture communication</li>
 *   <li>Handles bot action execution and market simulation lifecycle</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Create simulation with default settings
 * StockSim simulation = new StockSim();
 *
 * // Create stocks and traders
 * simulation.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology", "150.00");
 * simulation.createUser("user1", "John Doe", 10000);
 * simulation.createBot("bot1", "Trading Bot");
 *
 * // Place orders
 * Order buyOrder = new Order(Order.Side.BUY, "AAPL", new BigDecimal("150.00"), 10, "user1");
 * simulation.placeOrder(buyOrder);
 *
 * // Start simulation
 * simulation.startMarketSimulation();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see IMarket
 * @see IInstrumentRegistry
 * @see ITraderRegistry
 * @see IMarketSimulator
 * @see IModelSubject
 */
public class StockSim implements IModelSubject {
    /**
     * List of observers watching for model changes.
     * Part of the Observer pattern implementation.
     */
    private final List<IModelObserver> observers = new ArrayList<>();

    /**
     * Manages the currently selected stock and user in the UI.
     */
    private final SelectionManager selectionManager = new SelectionManager();

    /**
     * Market subsystem responsible for order matching and trade settlement.
     * Depends on abstraction (IMarket) following Dependency Inversion Principle.
     */
    private final IMarket market;

    /**
     * Registry managing all tradable instruments (stocks).
     * Depends on abstraction (IInstrumentRegistry) following DIP.
     */
    private final IInstrumentRegistry instrumentRegistry;

    /**
     * Registry managing all traders (users and bots).
     * Depends on abstraction (ITraderRegistry) following DIP.
     */
    private final ITraderRegistry traderRegistry;

    /**
     * Market simulator controlling the time-based simulation lifecycle.
     * Depends on abstraction (IMarketSimulator) following DIP.
     */
    private final IMarketSimulator marketSimulator;

    /**
     * Executor handling asynchronous bot trading actions.
     */
    private final BotActionExecutor botActionExecutor;

    /**
     * Constructs a StockSim with default configuration.
     * <ul>
     *   <li>Simulation speed: 3600 (1 simulated hour = 1 real second)</li>
     *   <li>Tick interval: 50ms</li>
     *   <li>Duration: 10 real seconds</li>
     * </ul>
     */
    public StockSim() {
        this(3600, 50, 10);
    }

    /**
     * Constructs a StockSim with custom simulation parameters.
     *
     * <p>This constructor initializes all subsystems in the correct order:
     * registries → market → bot executor → simulator. It also sets up
     * market callbacks for price updates and trade settlements.</p>
     *
     * @param simulationSpeed How many simulated seconds pass per real second
     * @param tickInterval Milliseconds between simulation ticks
     * @param durationInRealSeconds Total duration of simulation in real time
     */
    public StockSim(int simulationSpeed, int tickInterval, int durationInRealSeconds) {
        // Initialize registries
        this.instrumentRegistry = new InstrumentRegistry(new StockFactory());
        this.traderRegistry = new TraderRegistry(new UserFactory(), new BotFactory());

        // Initialize market
        this.market = new Market();

        // Initialize bot action executor
        this.botActionExecutor = new BotActionExecutor();

        // Set up market callbacks
        market.setOnPriceUpdate(this::notifyPriceUpdate);
        market.setOnTradeSettled(trade -> {
            notifyTradeSettled();

            String buyerTraderId = getTraderIdForOrder(trade.getBuyOrderId());
            String sellerTraderId = getTraderIdForOrder(trade.getSellOrderId());
            if (buyerTraderId != null && sellerTraderId != null) {
                Trader buyer = traderRegistry.getTrader(buyerTraderId);
                Trader seller = traderRegistry.getTrader(sellerTraderId);
                if (buyer instanceof User || seller instanceof User) {
                    notifyPortfolioChanged();
                }
            }
        });

        // Initialize simulator with configuration
        this.marketSimulator = new MarketSimulator(traderRegistry::getBots, this::onSimulationTick,
                this::saveStockPrices, simulationSpeed, tickInterval, durationInRealSeconds);
    }

    private String getTraderIdForOrder(int orderId) {
        // This is a workaround - ideally we'd have better order tracking
        for (Trader trader : traderRegistry.getAllTraders().values()) {
            if (trader instanceof User user) {
                if (user.getOrderHistory().getAllOrders().stream().anyMatch(o -> o.getOrderId() == orderId)) {
                    return trader.getId();
                }
            }
        }
        return null;
    }

    private void onSimulationTick() {
        // Execute bot trading decisions
        for (Bot bot : traderRegistry.getBots().values()) {
            bot.tick(this, botActionExecutor);
        }
        marketSimulator.setTotalTradesExecuted(market.getCompletedTrades().size());
    }

    public void addOrderBook(String symbol, OrderBook orderBook) {
        market.addOrderBook(symbol, orderBook);
    }

    public void removeOrderBook(String symbol) {
        market.removeOrderBook(symbol);
    }

    public OrderBook getOrderBook(String symbol) {
        return market.getOrderBook(symbol);
    }

    public void placeOrder(Order order) {
        market.placeOrder(order, traderRegistry.getAllTraders(), instrumentRegistry.getAllInstruments());
    }

    public void createStock(String symbol, String stockName, String tickSize, String lotSize, String category,
            String initialPrice) {
        instrumentRegistry.createInstrument(symbol, stockName, tickSize, lotSize, category, initialPrice);
    }

    public ArrayList<String> getCategories() {
        return instrumentRegistry.getCategories();
    }

    public HashMap<String, InstrumentDTO> getStocks() {
        HashMap<String, InstrumentDTO> result = new HashMap<>();

        for (var entry : instrumentRegistry.getAllInstruments().entrySet()) {
            result.put(entry.getKey(), StockMapper.toDto(entry.getValue()));
        }

        return result;
    }

    public HashMap<String, InstrumentDTO> getStocks(String category) {
        HashMap<String, InstrumentDTO> result = new HashMap<>();

        for (var entry : instrumentRegistry.getInstrumentsByCategory(category).entrySet()) {
            result.put(entry.getKey(), StockMapper.toDto(entry.getValue()));
        }

        return result;
    }

    public void createUser(String id, String name, int balance) {
        traderRegistry.createUser(id, name, balance);
    }

    public void createBot(String id, String name) {
        traderRegistry.createBot(id, name);
    }

    public void createBot(String id, String name, org.team27.stocksim.model.users.bot.IBotStrategy strategy) {
        traderRegistry.createBot(id, name, strategy);
    }

    public HashMap<String, Trader> getTraders() {
        return traderRegistry.getAllTraders();
    }

    public HashMap<String, Bot> getBots() {
        return traderRegistry.getBots();
    }

    public HashMap<String, User> getUsers() {
        return traderRegistry.getUsers();
    }

    public UserDTO getCurrentUserDto() {
        return traderRegistry.getCurrentUserDto();
    }

    public User getCurrentUser() {
        return traderRegistry.getCurrentUser();
    }

    public void setCurrentUser(String userId) {
        traderRegistry.setCurrentUser(userId);
    }

    public Portfolio createPortfolio(String id) {
        return traderRegistry.getTrader(id) != null ? traderRegistry.getTrader(id).getPortfolio() : null;
    }

    public void startMarketSimulation() {
        marketSimulator.start();
    }

    public void pauseMarketSimulation() {
        marketSimulator.pause();
    }

    public void stopMarketSimulation() {
        marketSimulator.stop();
        botActionExecutor.shutdown();
    }

    private void notifyPriceUpdate(Set<String> changedSymbols) {
        HashMap<String, InstrumentDTO> changedStocks = new HashMap<>();

        for (String symbol : changedSymbols) {
            Instrument instrument = instrumentRegistry.getAllInstruments().get(symbol);
            if (instrument != null) {
                changedStocks.put(symbol, StockMapper.toDto(instrument));
            }
        }

        for (IModelObserver o : observers) {
            o.onPriceUpdate(changedStocks);
        }
    }

    private void notifyTradeSettled() {
        for (IModelObserver o : observers) {
            o.onTradeSettled();
        }
    }

    private void notifyPortfolioChanged() {
        for (IModelObserver o : observers) {
            o.onPortfolioChanged();
        }
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }


    public void saveStockPrices() {
        StockPriceRepository repository = new StockPriceRepository();
        repository.saveStockPrices(instrumentRegistry.getAllInstruments());
    }


    public void saveBotPositions() {
        BotPositionRepository repository = new BotPositionRepository();
        repository.saveBotPositions(traderRegistry.getBots());
    }


    public Instrument getInstrument(String symbol) {
        return instrumentRegistry.getAllInstruments().get(symbol);
    }

    @Override
    public void addObserver(IModelObserver obs) {
        observers.add(obs);
    }

    @Override
    public void removeObserver(IModelObserver obs) {
        observers.remove(obs);
    }

}