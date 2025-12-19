package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Central registry for managing all tradable instruments in the simulation.
 *
 * <p>InstrumentRegistry implements the Registry pattern to provide centralized
 * management of all instruments (stocks) in the market. It uses a factory for
 * instrument creation, ensuring proper initialization and maintaining a
 * single source of truth for instrument data.</p>
 *
 * <p><strong>Design Patterns:</strong> Registry + Factory + Singleton-like behavior</p>
 * <ul>
 *   <li>Centralized instrument storage and retrieval</li>
 *   <li>Delegates creation to IInstrumentFactory</li>
 *   <li>Prevents duplicate symbols (case-insensitive)</li>
 *   <li>Supports category-based filtering</li>
 *   <li>Provides type-safe access to instrument catalog</li>
 * </ul>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Case-insensitive symbol handling (auto-uppercase)</li>
 *   <li>Category-based organization (Technology, Finance, etc.)</li>
 *   <li>Duplicate prevention</li>
 *   <li>Filtered retrieval by category</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * IInstrumentFactory factory = new StockFactory();
 * InstrumentRegistry registry = new InstrumentRegistry(factory);
 *
 * // Create instruments
 * registry.createInstrument("AAPL", "Apple Inc.", "0.01", "1",
 *                           "Technology", "150.00");
 * registry.createInstrument("JPM", "JPMorgan Chase", "0.01", "1",
 *                           "Finance", "145.00");
 *
 * // Retrieve instruments
 * Instrument apple = registry.getInstrument("aapl"); // Case-insensitive
 * HashMap<String, Instrument> techStocks = registry.getInstrumentsByCategory("Technology");
 *
 * // Get all categories
 * ArrayList<String> categories = registry.getCategories();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see IInstrumentRegistry
 * @see Instrument
 * @see IInstrumentFactory
 * @see ECategory
 */
public class InstrumentRegistry implements IInstrumentRegistry {

    /**
     * Map of instrument symbols to instrument instances.
     * Keys are always uppercase for case-insensitive lookup.
     */
    private final HashMap<String, Instrument> instruments;

    /**
     * Factory used to create new instrument instances.
     */
    private final IInstrumentFactory instrumentFactory;

    /**
     * Constructs an InstrumentRegistry with the specified factory.
     *
     * @param instrumentFactory Factory for creating instrument instances
     */
    public InstrumentRegistry(IInstrumentFactory instrumentFactory) {
        this.instruments = new HashMap<>();
        this.instrumentFactory = instrumentFactory;
    }

    /**
     * Creates and registers a new instrument.
     *
     * <p>Symbol is automatically converted to uppercase. Returns false if
     * an instrument with the symbol already exists.</p>
     *
     * @param symbol Unique instrument symbol
     * @param stockName Human-readable name
     * @param tickSize Minimum price increment as string
     * @param lotSize Minimum trading quantity as string
     * @param category Stock category
     * @param initialPrice Starting price as string
     * @return true if created successfully, false if symbol already exists
     */
    @Override
    public boolean createInstrument(String symbol, String stockName, String tickSize, String lotSize, String category,
            String initialPrice) {
        String highSymbol = symbol.toUpperCase();

        if (instruments.containsKey(highSymbol)) {
            return false;
        }

        Instrument instrument = instrumentFactory.createInstrument(
                highSymbol,
                stockName,
                new BigDecimal(tickSize),
                Integer.parseInt(lotSize),
                category,
                new BigDecimal(initialPrice));

        instruments.put(highSymbol, instrument);
        return true;
    }

    /**
     * Retrieves all registered instruments.
     *
     * @return HashMap of all instruments keyed by symbol
     */
    @Override
    public HashMap<String, Instrument> getAllInstruments() {
        return instruments;
    }

    /**
     * Retrieves instruments filtered by category.
     *
     * <p>If category is "All", returns all instruments. Otherwise returns
     * only instruments matching the specified category.</p>
     *
     * @param category Category to filter by, or "All" for all instruments
     * @return HashMap of filtered instruments
     */
    @Override
    public HashMap<String, Instrument> getInstrumentsByCategory(String category) {
        if (category.equals("All")) {
            return instruments;
        }

        HashMap<String, Instrument> filtered = new HashMap<>();
        for (Instrument instrument : instruments.values()) {
            if (instrument.getCategory().equals(category)) {
                filtered.put(instrument.getSymbol(), instrument);
            }
        }
        return filtered;
    }

    /**
     * Gets all available category labels.
     *
     * @return ArrayList of category names from ECategory enum
     */
    @Override
    public ArrayList<String> getCategories() {
        ArrayList<String> categoryLabels = new ArrayList<>();
        for (ECategory category : ECategory.values()) {
            categoryLabels.add(category.getLabel());
        }
        return categoryLabels;
    }

    /**
     * Retrieves a specific instrument by symbol.
     *
     * <p>Lookup is case-insensitive (symbol converted to uppercase).</p>
     *
     * @param symbol The instrument symbol to lookup
     * @return The instrument, or null if not found
     */
    @Override
    public Instrument getInstrument(String symbol) {
        return instruments.get(symbol.toUpperCase());
    }

    /**
     * Checks if an instrument with the given symbol exists.
     *
     * <p>Check is case-insensitive.</p>
     *
     * @param symbol The symbol to check
     * @return true if instrument exists, false otherwise
     */
    @Override
    public boolean hasInstrument(String symbol) {
        return instruments.containsKey(symbol.toUpperCase());
    }

}
