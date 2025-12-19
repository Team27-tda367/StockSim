package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Maintains historical price data for an instrument.
 *
 * <p>PriceHistory stores a time-series of price points, enabling technical
 * analysis, charting, and performance tracking. Each price point includes
 * both the price and timestamp, allowing for precise historical reconstruction.</p>
 *
 * <p><strong>Design Pattern:</strong> Repository + Encapsulation</p>
 * <ul>
 *   <li>Stores chronological sequence of price points</li>
 *   <li>Returns defensive copies to maintain immutability</li>
 *   <li>Supports technical analysis and charting</li>
 *   <li>Enables momentum calculation and trend analysis</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * PriceHistory history = new PriceHistory();
 *
 * // Add price updates over time
 * history.addPrice(new BigDecimal("150.00"), 1000000L);
 * history.addPrice(new BigDecimal("151.50"), 1001000L);
 * history.addPrice(new BigDecimal("152.25"), 1002000L);
 *
 * // Retrieve historical data
 * List<PricePoint> points = history.getPoints();
 *
 * // Analyze price movement
 * if (points.size() >= 2) {
 *     BigDecimal firstPrice = points.get(0).getPrice();
 *     BigDecimal lastPrice = points.get(points.size() - 1).getPrice();
 *     BigDecimal change = lastPrice.subtract(firstPrice);
 *     System.out.println("Price change: $" + change);
 * }
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see PricePoint
 * @see Stock
 * @see PriceHistoryService
 */
public class PriceHistory {
    /**
     * Chronological list of price points.
     */
    private List<PricePoint> points;

    /**
     * Constructs an empty PriceHistory.
     */
    public PriceHistory() {
        this.points = new ArrayList<>();
    }

    /**
     * Adds a new price point to the history.
     *
     * @param price The price at this point in time
     * @param timestamp Unix timestamp in milliseconds
     */
    public void addPrice(BigDecimal price, long timestamp) {
        PricePoint point = new PricePoint(timestamp, price);
        points.add(point);
    }

    /**
     * Returns a defensive copy of all price points.
     *
     * <p>Returns a new list to prevent external modification of internal state.</p>
     *
     * @return List of all price points in chronological order
     */
    public List<PricePoint> getPoints() {
        return new ArrayList<>(points); // Return a copy to maintain encapsulation
    }

}
