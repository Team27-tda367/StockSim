package org.team27.stocksim.model.instruments;

import org.team27.stocksim.model.clock.ClockProvider;

import java.math.BigDecimal;

/**
 * Represents a tradable stock instrument in the market.
 *
 * <p>Stock extends Instrument and adds real-time price tracking with historical
 * data. It uses ClockProvider for timestamp management, making the class
 * testable with simulated time.</p>
 *
 * <p><strong>Design Pattern:</strong> Inheritance + Observer (via price updates)</p>
 * <ul>
 *   <li>Tracks current price and complete price history</li>
 *   <li>Uses ClockProvider for testable timestamp management</li>
 *   <li>Inherits tick size, lot size, and category from Instrument</li>
 *   <li>Price history enables charting and technical analysis</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Stocks are created through StockFactory
 * StockFactory factory = new StockFactory();
 * Stock apple = (Stock) factory.create(
 *     "AAPL",
 *     "Apple Inc.",
 *     new BigDecimal("0.01"),
 *     1,
 *     "Technology",
 *     new BigDecimal("150.00")
 * );
 *
 * // Update price when trades occur
 * apple.setCurrentPrice(new BigDecimal("151.50"));
 *
 * // Access price data
 * BigDecimal currentPrice = apple.getCurrentPrice();
 * PriceHistory history = apple.getPriceHistory();
 * List<PricePoint> recentPrices = history.getRecentPrices(100);
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Instrument
 * @see PriceHistory
 * @see StockFactory
 * @see ClockProvider
 */
public class Stock extends Instrument {
    /**
     * Current market price of the stock.
     */
    private BigDecimal price;

    /**
     * Complete historical price data for charting and analysis.
     */
    private PriceHistory priceHistory;

    /**
     * Package-private constructor for use by StockFactory.
     *
     * @param symbol Unique stock symbol (e.g., "AAPL")
     * @param name Company name
     * @param tickSize Minimum price increment
     * @param lotSize Minimum trading quantity
     * @param category Stock category (e.g., "Technology", "Finance")
     * @param initialPrice Starting price
     */
    Stock(String symbol, String name, BigDecimal tickSize, int lotSize, String category, BigDecimal initialPrice) {
        super(symbol, name, tickSize, lotSize, category);
        this.price = initialPrice;
        this.priceHistory = new PriceHistory();
    }

    @Override
    public BigDecimal getCurrentPrice() {
        return price;
    }

    public void setCurrentPrice(BigDecimal price) {
        setCurrentPrice(price, ClockProvider.currentTimeMillis());
    }

    public void setCurrentPrice(BigDecimal price, long timestamp) {
        this.price = price;
        priceHistory.addPrice(price, timestamp);
    }

    public PriceHistory getPriceHistory() {
        return priceHistory;
    }

}
