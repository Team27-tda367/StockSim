package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;

/**
 * Represents a single price observation at a specific point in time.
 *
 * <p>PricePoint is an immutable value object that pairs a price with its
 * timestamp, forming the basic unit of price history data. These points are
 * used to build time-series data for charting, technical analysis, and
 * historical price tracking.</p>
 *
 * <p><strong>Design Pattern:</strong> Value Object (Immutable)</p>
 * <ul>
 *   <li>Immutable price-time pair</li>
 *   <li>Used in chronological sequences for price history</li>
 *   <li>Enables technical analysis and charting</li>
 *   <li>Lightweight data structure</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Price points are typically created by PriceHistory
 * PricePoint point = new PricePoint(
 *     System.currentTimeMillis(),
 *     new BigDecimal("150.00")
 * );
 *
 * // Access data
 * long when = point.getTimestamp();
 * BigDecimal price = point.getPrice();
 *
 * // Used in collections for analysis
 * List<PricePoint> history = stock.getPriceHistory().getPoints();
 * for (PricePoint p : history) {
 *     plotChart(p.getTimestamp(), p.getPrice());
 * }
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see PriceHistory
 * @see Stock
 */
public class PricePoint {
    /**
     * Unix timestamp in milliseconds when this price was recorded.
     */
    private long timestamp;

    /**
     * The price at this point in time.
     */
    private BigDecimal price;

    /**
     * Constructs a PricePoint with the specified timestamp and price.
     *
     * @param timestamp Unix timestamp in milliseconds
     * @param price The price at this time
     */
    public PricePoint(long timestamp, BigDecimal price) {
        this.timestamp = timestamp;
        this.price = price;
    }

    /**
     * Gets the timestamp of this price point.
     *
     * @return Unix timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the price at this point.
     *
     * @return The price
     */
    public BigDecimal getPrice() {
        return price;
    }
}
