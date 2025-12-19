package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;

/**
 * Factory for creating Stock instrument instances.
 *
 * <p>This factory implements the Factory Method pattern to encapsulate stock
 * creation logic. It ensures that Stock objects are created with proper
 * initialization and can be extended to support additional instrument types
 * without modifying client code.</p>
 *
 * <p><strong>Design Pattern:</strong> Factory Method</p>
 * <ul>
 *   <li>Encapsulates object creation logic</li>
 *   <li>Implements IInstrumentFactory for polymorphic factory usage</li>
 *   <li>Enables extension for future instrument types</li>
 *   <li>Separates construction from representation</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * IInstrumentFactory factory = new StockFactory();
 *
 * Instrument apple = factory.createInstrument(
 *     "AAPL",
 *     "Apple Inc.",
 *     new BigDecimal("0.01"),  // tick size
 *     1,                        // lot size
 *     "Technology",
 *     new BigDecimal("150.00")  // initial price
 * );
 *
 * // Factory ensures proper Stock initialization
 * Stock stock = (Stock) apple;
 * PriceHistory history = stock.getPriceHistory();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see IInstrumentFactory
 * @see Stock
 * @see InstrumentRegistry
 */
public class StockFactory implements IInstrumentFactory {
    /**
     * Creates a new Stock instance with the specified parameters.
     *
     * @param symbol Unique stock symbol (e.g., "AAPL")
     * @param name Company/stock name
     * @param tickSize Minimum price increment
     * @param lotSize Minimum trading quantity
     * @param category Stock category (e.g., "Technology", "Finance")
     * @param initialPrice Starting price
     * @return A new Stock instrument
     */
    @Override
    public Instrument createInstrument(String symbol, String name, BigDecimal tickSize, int lotSize, String category,
            BigDecimal initialPrice) {
        return new Stock(symbol, name, tickSize, lotSize, category, initialPrice);
    }
}
