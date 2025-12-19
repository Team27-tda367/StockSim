package org.team27.stocksim.model.market;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a completed trade between two orders.
 *
 * <p>A trade is created when a buy order and a sell order match in the market.
 * It records the execution price, quantity, and the orders involved. Trades
 * are immutable once created and serve as the historical record of market
 * transactions.</p>
 *
 * <p><strong>Design Pattern:</strong> Value Object (Immutable)</p>
 * <ul>
 *   <li>All fields are final and immutable after construction</li>
 *   <li>Records the complete context of a trade execution</li>
 *   <li>Links buy and sell orders through order IDs</li>
 *   <li>Timestamps enable historical price analysis</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Trade created by matching engine when orders match
 * Trade trade = new Trade(
 *     buyOrder.getOrderId(),
 *     sellOrder.getOrderId(),
 *     "AAPL",
 *     new BigDecimal("150.00"),
 *     50,
 *     Instant.now()
 * );
 *
 * System.out.println("Traded " + trade.getQuantity() +
 *                    " shares of " + trade.getStockSymbol() +
 *                    " at $" + trade.getPrice());
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Order
 * @see MatchingEngine
 * @see SettlementEngine
 */
public class Trade {
    /**
     * Symbol of the stock/instrument traded.
     */
    private final String stockSymbol;

    /**
     * Execution price of the trade.
     */
    private final BigDecimal price;

    /**
     * Number of shares/units traded.
     */
    private final int quantity;

    /**
     * ID of the buy order involved in this trade.
     */
    private final int buyOrderId;

    /**
     * ID of the sell order involved in this trade.
     */
    private final int sellOrderId;

    /**
     * Timestamp when the trade was executed.
     */
    private final Instant time;

    /**
     * Constructs a new Trade recording a completed transaction.
     *
     * @param buyOrderId ID of the buy order
     * @param sellOrderId ID of the sell order
     * @param stockSymbol Symbol of the traded instrument
     * @param price Execution price
     * @param quantity Number of units traded
     * @param time Timestamp of execution
     */
    public Trade(int buyOrderId, int sellOrderId, String stockSymbol, BigDecimal price, int quantity, Instant time) {

        this.stockSymbol = stockSymbol;
        this.price = price;
        this.quantity = quantity;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.time = time;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getBuyOrderId() {
        return buyOrderId;
    }

    public int getSellOrderId() {
        return sellOrderId;
    }

    public Instant getTime() {
        return time;
    }
}
