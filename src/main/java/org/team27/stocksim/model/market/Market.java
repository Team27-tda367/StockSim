package org.team27.stocksim.model.market;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.Trader;
import org.team27.stocksim.model.users.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Central marketplace for order matching and trade execution.
 *
 * <p>This class implements the core market functionality, coordinating between
 * order books, matching engine, and settlement engine. It ensures thread-safe
 * operations using concurrent collections and synchronized blocks at the order
 * book level.</p>
 *
 * <p><strong>Design Patterns:</strong> Facade + Observer + Strategy</p>
 * <ul>
 *   <li>Coordinates OrderBook, MatchingEngine, and SettlementEngine subsystems</li>
 *   <li>Uses callback functions (Observer) for price updates and trade settlements</li>
 *   <li>Validates orders before processing to ensure market integrity</li>
 *   <li>Thread-safe operations support concurrent trading by multiple bots/users</li>
 * </ul>
 *
 * <h2>Order Processing Flow:</h2>
 * <ol>
 *   <li>Order validation via OrderValidator</li>
 *   <li>Order recording in trader's history</li>
 *   <li>Synchronized matching against order book</li>
 *   <li>Trade settlement with atomic portfolio updates</li>
 *   <li>Price update notifications to observers</li>
 * </ol>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * Market market = new Market();
 * market.setOnPriceUpdate(symbols -> System.out.println("Prices updated: " + symbols));
 *
 * Order buyOrder = new Order(Order.Side.BUY, "AAPL", new BigDecimal("150.00"), 10, "trader1");
 * market.placeOrder(buyOrder, tradersMap, instrumentsMap);
 *
 * OrderBook orderBook = market.getOrderBook("AAPL");
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see IMarket
 * @see OrderBook
 * @see MatchingEngine
 * @see SettlementEngine
 * @see OrderValidator
 */
public class Market implements IMarket {

    /**
     * Map of stock symbols to their corresponding order books.
     * Uses ConcurrentHashMap for thread-safe access.
     */
    private final ConcurrentHashMap<String, OrderBook> orderBooks;

    /**
     * Engine responsible for matching buy and sell orders.
     */
    private final MatchingEngine matchingEngine;

    /**
     * Engine responsible for settling trades and updating portfolios.
     */
    private final SettlementEngine settlementEngine;

    /**
     * Thread-safe list of all completed trades in the market.
     */
    private final List<Trade> completedTrades;

    /**
     * Map tracking which trader placed which order for settlement purposes.
     */
    private final ConcurrentHashMap<Integer, String> orderIdToTraderId;

    /**
     * Validator ensuring order integrity before processing.
     */
    private final OrderValidator orderValidator;

    /**
     * Callback invoked when stock prices are updated.
     */
    private Consumer<Set<String>> onPriceUpdate;

    /**
     * Callback invoked when a trade is settled.
     */
    private Consumer<Trade> onTradeSettled;

    /**
     * Constructs a new Market with all necessary subsystems initialized.
     *
     * <p>Initializes concurrent collections for thread-safety and creates
     * the matching engine, settlement engine, and order validator.</p>
     */
    public Market() {
        this.orderBooks = new ConcurrentHashMap<>();
        this.matchingEngine = new MatchingEngine();
        this.completedTrades = new CopyOnWriteArrayList<>();
        this.orderIdToTraderId = new ConcurrentHashMap<>();
        this.settlementEngine = new SettlementEngine(orderIdToTraderId, this::handleTradeSettled);
        this.orderValidator = new OrderValidator();
    }

    @Override
    public void placeOrder(Order order, HashMap<String, Trader> traders, HashMap<String, Instrument> stocks) {
        // Phase 1.2: Validate order before processing
        OrderValidator.ValidationResult validationResult = orderValidator.validate(order);
        if (!validationResult.isValid()) {
            // Log validation failure and reject order
            System.err.println("Order validation failed: " + validationResult.getErrorMessage() +
                             " for order " + (order != null ? order.getOrderId() : "null"));
            return; // Reject invalid order
        }

        settlementEngine.trackOrder(order.getOrderId(), order.getTraderId());

        recordOrderInHistory(order, traders);


        processOrder(order, traders, stocks);
    }

    private void recordOrderInHistory(Order order, HashMap<String, Trader> traders) {
        Trader trader = traders.get(order.getTraderId());
        if (trader instanceof User) {
            ((User) trader).getOrderHistory().addOrder(order);
        }
    }

    private void processOrder(Order order, HashMap<String, Trader> traders, HashMap<String, Instrument> stocks) {
        OrderBook orderBook = getOrderBook(order.getSymbol());

        // Synchronize at the orderBook level to ensure atomic matching and settlement
        synchronized (orderBook) {
            List<Trade> trades = matchingEngine.match(order, orderBook);

            Set<String> affectedSymbols = new HashSet<>();
            for (Trade trade : trades) {
                completedTrades.add(trade);
                boolean settled = settlementEngine.settleTrade(trade, traders, stocks);
                if (settled) {
                    affectedSymbols.add(trade.getStockSymbol());
                }
            }

            if (!affectedSymbols.isEmpty() && onPriceUpdate != null) {
                onPriceUpdate.accept(affectedSymbols);
            }
        }
    }

    private void handleTradeSettled(Trade trade) {
        if (onTradeSettled != null) {
            onTradeSettled.accept(trade);
        }
    }

    @Override
    public void addOrderBook(String symbol, OrderBook orderBook) {
        orderBooks.put(symbol, orderBook);
    }

    @Override
    public void removeOrderBook(String symbol) {
        orderBooks.remove(symbol);
    }

    @Override
    public OrderBook getOrderBook(String symbol) {
        return orderBooks.computeIfAbsent(symbol, OrderBook::new);
    }

    @Override
    public List<Trade> getCompletedTrades() {
        return new ArrayList<>(completedTrades);
    }

    @Override
    public void setOnPriceUpdate(Consumer<Set<String>> callback) {
        this.onPriceUpdate = callback;
    }

    @Override
    public void setOnTradeSettled(Consumer<Trade> callback) {
        this.onTradeSettled = callback;
    }
}
