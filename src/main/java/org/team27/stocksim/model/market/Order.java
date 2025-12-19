package org.team27.stocksim.model.market;

import org.team27.stocksim.model.clock.ClockProvider;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a trading order in the market.
 *
 * <p>An order represents a trader's intent to buy or sell a specific quantity
 * of an instrument at a specified price. Orders can be partially filled over
 * time and track their remaining quantity and status.</p>
 *
 * <p><strong>Design Patterns:</strong> Value Object + Factory Method</p>
 * <ul>
 *   <li>Immutable price, quantity, symbol, and trader ID ensure data integrity</li>
 *   <li>Thread-safe order ID generation using synchronized method</li>
 *   <li>Automatic status tracking based on fill quantity</li>
 *   <li>Timestamps using ClockProvider for testability</li>
 * </ul>
 *
 * <h2>Order Lifecycle:</h2>
 * <ol>
 *   <li>NEW - Order created but not yet filled</li>
 *   <li>PARTIALLY_FILLED - Some quantity filled, remainder pending</li>
 *   <li>FILLED - Entire quantity filled</li>
 *   <li>CANCELLED - Order cancelled before complete fill</li>
 * </ol>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Create a limit buy order
 * Order buyOrder = new Order(
 *     Order.Side.BUY,
 *     "AAPL",
 *     new BigDecimal("150.00"),
 *     100,
 *     "trader1"
 * );
 *
 * // Create a market sell order
 * Order sellOrder = new Order(
 *     Order.Side.SELL,
 *     Order.OrderType.MARKET,
 *     "AAPL",
 *     new BigDecimal("0"), // Market orders ignore price
 *     50,
 *     "trader2"
 * );
 *
 * // Fill order partially
 * buyOrder.fill(30);
 * System.out.println(buyOrder.getStatus()); // PARTIALLY_FILLED
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see OrderBook
 * @see Trade
 * @see MatchingEngine
 */
public class Order {
    /**
     * Next available order ID. Incremented atomically for each new order.
     */
    private static int nextOrderId = 1;

    /**
     * Whether this is a buy or sell order.
     */
    private final Side side;

    /**
     * Type of order (LIMIT or MARKET).
     */
    private final OrderType orderType;

    /**
     * Unique identifier for this order.
     */
    private final int orderId;

    /**
     * Limit price for the order. Ignored for market orders.
     */
    private final BigDecimal price;

    /**
     * Total quantity requested in the order.
     */
    private final int totalQuantity;

    /**
     * Timestamp when the order was created.
     */
    private final Instant timeStamp;

    /**
     * Symbol of the instrument being traded.
     */
    private final String instrumentSymbol;

    /**
     * ID of the trader who placed this order.
     */
    private final String traderId;

    /**
     * Current status of the order.
     */
    private Status status = Status.NEW;

    /**
     * Quantity not yet filled.
     */
    private int remainingQuantity;

    /**
     * Constructs a limit order.
     *
     * @param side Whether this is a BUY or SELL order
     * @param instrumentSymbol Symbol of the instrument to trade
     * @param price Limit price for the order
     * @param quantity Number of shares/units to trade
     * @param traderId ID of the trader placing the order
     */
    public Order(Side side, String instrumentSymbol, BigDecimal price, int quantity, String traderId) {
        this(side, OrderType.LIMIT, instrumentSymbol, price, quantity, traderId);
    }

    /**
     * Constructs an order with specified type (LIMIT or MARKET).
     *
     * <p>This constructor initializes the order with a unique ID generated
     * atomically and captures the current timestamp from ClockProvider.</p>
     *
     * @param side Whether this is a BUY or SELL order
     * @param orderType Type of order (LIMIT or MARKET)
     * @param instrumentSymbol Symbol of the instrument to trade
     * @param price Limit price (ignored for MARKET orders)
     * @param quantity Number of shares/units to trade
     * @param traderId ID of the trader placing the order
     */
    public Order(Side side, OrderType orderType, String instrumentSymbol, BigDecimal price, int quantity,
            String traderId) {
        this.side = side;
        this.orderType = orderType;
        this.instrumentSymbol = instrumentSymbol;
        this.orderId = generateOrderId();
        this.price = price;
        this.totalQuantity = quantity;
        this.remainingQuantity = quantity;
        this.traderId = traderId;

        this.timeStamp = ClockProvider.getClock().instant();
    }

    private static synchronized int generateOrderId() {
        return nextOrderId++;
    }

    public int getOrderId() {
        return orderId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public String getSymbol() {
        return instrumentSymbol;
    }

    public Side getSide() {
        return side;
    }

    public Status getStatus() {
        updateStatus();
        return status;
    }

    public String getTraderId() {
        return traderId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public boolean isMarketOrder() {
        return orderType == OrderType.MARKET;
    }

    public void cancel() {// TODO
        status = Status.CANCELLED;
    }

    public void fill(int quantity) {
        remainingQuantity = remainingQuantity - quantity;
        updateStatus();
    }

    public boolean isBuyOrder() {
        return side == Side.BUY;
    }

    public boolean isFilled() {
        updateStatus();
        return status == Status.FILLED;
    }

    private void updateStatus() {
        if (remainingQuantity == 0) {
            status = Status.FILLED;
        } else if (remainingQuantity < totalQuantity) {
            status = Status.PARTIALLY_FILLED;
        }
    }

    public enum Side {
        BUY, SELL
    }

    public enum Status {
        NEW, PARTIALLY_FILLED, FILLED, CANCELLED
    }

    public enum OrderType {
        LIMIT, MARKET
    }

}
