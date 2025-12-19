package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;

/**
 * Abstract base class for all tradable instruments.
 *
 * <p>Instrument provides the common attributes and interface for all financial
 * instruments that can be traded in the market. It defines the contract for
 * price management and instrument identification while allowing subclasses to
 * implement specific instrument behaviors.</p>
 *
 * <p><strong>Design Pattern:</strong> Template Method</p>
 * <ul>
 *   <li>Common attributes: symbol, name, tick size, lot size, category</li>
 *   <li>Abstract price management methods for subclass implementation</li>
 *   <li>Supports extension for different instrument types (stocks, bonds, etc.)</li>
 *   <li>Enforces consistent instrument interface across types</li>
 * </ul>
 *
 * <h2>Key Concepts:</h2>
 * <ul>
 *   <li><strong>Tick Size:</strong> Minimum price increment (e.g., $0.01)</li>
 *   <li><strong>Lot Size:</strong> Minimum trading quantity (e.g., 1 share)</li>
 *   <li><strong>Category:</strong> Classification (e.g., "Technology", "Finance")</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Instruments are typically created via factories
 * IInstrumentFactory factory = new StockFactory();
 * Instrument instrument = factory.createInstrument(
 *     "AAPL",
 *     "Apple Inc.",
 *     new BigDecimal("0.01"),
 *     1,
 *     "Technology",
 *     new BigDecimal("150.00")
 * );
 *
 * // Access common properties
 * String symbol = instrument.getSymbol();
 * BigDecimal price = instrument.getCurrentPrice();
 * PriceHistory history = instrument.getPriceHistory();
 *
 * // Update price
 * instrument.setCurrentPrice(new BigDecimal("151.50"));
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Stock
 * @see IInstrumentFactory
 * @see InstrumentRegistry
 */
public abstract class Instrument {
    /**
     * Unique instrument symbol (e.g., "AAPL").
     */
    protected final String symbol;

    /**
     * Human-readable instrument name.
     */
    protected final String name;

    /**
     * Minimum price increment for this instrument.
     */
    protected final BigDecimal tickSize;

    /**
     * Minimum trading quantity.
     */
    protected final int lotSize;

    /**
     * Instrument category/sector.
     */
    protected final String category;

    /**
     * Constructs an Instrument with the specified attributes.
     *
     * @param symbol Unique identifier
     * @param name Display name
     * @param tickSize Minimum price increment
     * @param lotSize Minimum trading quantity
     * @param category Classification category
     */
    public Instrument(String symbol, String name, BigDecimal tickSize, int lotSize, String category) {
        this.symbol = symbol;
        this.name = name;
        this.tickSize = tickSize;
        this.lotSize = lotSize;
        this.category = category;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getTickSize() {
        return tickSize;
    }

    public int getLotSize() {
        return lotSize;
    }

    public String getCategory() {
        return category;
    }

    public abstract void setCurrentPrice(BigDecimal price);

    public abstract void setCurrentPrice(BigDecimal price, long timestamp);

    public abstract BigDecimal getCurrentPrice();

    public abstract PriceHistory getPriceHistory();

}
