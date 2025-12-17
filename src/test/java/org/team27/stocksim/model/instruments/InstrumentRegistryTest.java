package org.team27.stocksim.model.instruments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

@DisplayName("InstrumentRegistry Tests")
class InstrumentRegistryTest {

    private InstrumentRegistry registry;
    private IInstrumentFactory factory;

    @BeforeEach
    void setUp() {
        factory = new StockFactory();
        registry = new InstrumentRegistry(factory);
    }

    @Test
    @DisplayName("Should create empty registry")
    void testCreateEmptyRegistry() {
        assertNotNull(registry);
        assertTrue(registry.getAllInstruments().isEmpty());
    }

    @Test
    @DisplayName("Should create instrument successfully")
    void testCreateInstrument() {
        boolean result = registry.createInstrument("AAPL", "Apple Inc.", "0.01", "1", "Technology");

        assertTrue(result);
        assertTrue(registry.hasInstrument("AAPL"));
    }

    @Test
    @DisplayName("Should retrieve created instrument")
    void testRetrieveInstrument() {
        registry.createInstrument("AAPL", "Apple Inc.", "0.01", "1", "Technology");

        Instrument instrument = registry.getInstrument("AAPL");

        assertNotNull(instrument);
        assertEquals("AAPL", instrument.getSymbol());
        assertEquals("Apple Inc.", instrument.getName());
    }

    @Test
    @DisplayName("Should not create duplicate instruments")
    void testPreventDuplicateInstruments() {
        boolean first = registry.createInstrument("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        boolean second = registry.createInstrument("AAPL", "Apple Inc.", "0.01", "1", "Technology");

        assertTrue(first);
        assertFalse(second);
    }

    @Test
    @DisplayName("Should handle case-insensitive symbols")
    void testCaseInsensitiveSymbols() {
        registry.createInstrument("aapl", "Apple Inc.", "0.01", "1", "Technology");

        assertTrue(registry.hasInstrument("AAPL"));
        assertTrue(registry.hasInstrument("aapl"));
        assertNotNull(registry.getInstrument("AAPL"));
        assertNotNull(registry.getInstrument("aapl"));
    }

    @Test
    @DisplayName("Should prevent duplicate with different case")
    void testPreventDuplicateDifferentCase() {
        boolean first = registry.createInstrument("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        boolean second = registry.createInstrument("aapl", "Apple Inc.", "0.01", "1", "Technology");

        assertTrue(first);
        assertFalse(second);
    }

    @Test
    @DisplayName("Should create multiple different instruments")
    void testCreateMultipleInstruments() {
        registry.createInstrument("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        registry.createInstrument("GOOGL", "Google", "0.01", "1", "Technology");
        registry.createInstrument("MSFT", "Microsoft", "0.01", "1", "Technology");

        assertEquals(3, registry.getAllInstruments().size());
    }

    @Test
    @DisplayName("Should get all instruments")
    void testGetAllInstruments() {
        registry.createInstrument("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        registry.createInstrument("GOOGL", "Google", "0.01", "1", "Technology");

        HashMap<String, Instrument> allInstruments = registry.getAllInstruments();

        assertEquals(2, allInstruments.size());
        assertTrue(allInstruments.containsKey("AAPL"));
        assertTrue(allInstruments.containsKey("GOOGL"));
    }

    @Test
    @DisplayName("Should filter instruments by category")
    void testFilterByCategory() {
        registry.createInstrument("AAPL", "Apple", "0.01", "1", "Technology");
        registry.createInstrument("GOOGL", "Google", "0.01", "1", "Technology");
        registry.createInstrument("JPM", "JP Morgan", "0.01", "1", "Finance");
        registry.createInstrument("BAC", "Bank of America", "0.01", "1", "Finance");

        HashMap<String, Instrument> techStocks = registry.getInstrumentsByCategory("Technology");
        HashMap<String, Instrument> financeStocks = registry.getInstrumentsByCategory("Finance");

        assertEquals(2, techStocks.size());
        assertEquals(2, financeStocks.size());
        assertTrue(techStocks.containsKey("AAPL"));
        assertTrue(financeStocks.containsKey("JPM"));
    }

    @Test
    @DisplayName("Should return all instruments when filtering by 'All'")
    void testFilterByAllCategory() {
        registry.createInstrument("AAPL", "Apple", "0.01", "1", "Technology");
        registry.createInstrument("JPM", "JP Morgan", "0.01", "1", "Finance");

        HashMap<String, Instrument> allInstruments = registry.getInstrumentsByCategory("All");

        assertEquals(2, allInstruments.size());
    }

    @Test
    @DisplayName("Should return empty map for non-existent category")
    void testFilterByNonExistentCategory() {
        registry.createInstrument("AAPL", "Apple", "0.01", "1", "Technology");

        HashMap<String, Instrument> result = registry.getInstrumentsByCategory("NonExistent");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should get available categories")
    void testGetCategories() {
        var categories = registry.getCategories();

        assertNotNull(categories);
        assertFalse(categories.isEmpty());
        // Should contain all ECategory enum values
    }

    @Test
    @DisplayName("Should return null for non-existent instrument")
    void testGetNonExistentInstrument() {
        Instrument instrument = registry.getInstrument("NONEXISTENT");

        assertNull(instrument);
    }

    @Test
    @DisplayName("Should return false for non-existent instrument check")
    void testHasNonExistentInstrument() {
        boolean result = registry.hasInstrument("NONEXISTENT");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle instruments with different tick sizes")
    void testDifferentTickSizes() {
        registry.createInstrument("AAPL", "Apple", "0.01", "1", "Technology");
        registry.createInstrument("GOOGL", "Google", "0.05", "1", "Technology");

        Instrument aapl = registry.getInstrument("AAPL");
        Instrument googl = registry.getInstrument("GOOGL");

        assertEquals(money("0.01"), aapl.getTickSize());
        assertEquals(money("0.05"), googl.getTickSize());
    }

    @Test
    @DisplayName("Should handle instruments with different lot sizes")
    void testDifferentLotSizes() {
        registry.createInstrument("AAPL", "Apple", "0.01", "1", "Technology");
        registry.createInstrument("GOOGL", "Google", "0.01", "100", "Technology");

        Instrument aapl = registry.getInstrument("AAPL");
        Instrument googl = registry.getInstrument("GOOGL");

        assertEquals(1, aapl.getLotSize());
        assertEquals(100, googl.getLotSize());
    }

    @Test
    @DisplayName("Should create instruments with valid parameters")
    void testCreateWithValidParameters() {
        boolean result = registry.createInstrument(
            "AAPL",
            "Apple Inc.",
            "0.01",
            "1",
            "Technology"
        );

        assertTrue(result);
        Instrument instrument = registry.getInstrument("AAPL");
        assertNotNull(instrument);
        assertEquals("AAPL", instrument.getSymbol());
        assertEquals("Apple Inc.", instrument.getName());
        assertEquals(money("0.01"), instrument.getTickSize());
        assertEquals(1, instrument.getLotSize());
        assertEquals("Technology", instrument.getCategory());
    }

    @Test
    @DisplayName("Should handle many instruments")
    void testManyInstruments() {
        for (int i = 0; i < 100; i++) {
            registry.createInstrument(
                "STOCK" + i,
                "Stock " + i,
                "0.01",
                "1",
                "Technology"
            );
        }

        assertEquals(100, registry.getAllInstruments().size());
        assertTrue(registry.hasInstrument("STOCK0"));
        assertTrue(registry.hasInstrument("STOCK99"));
    }

    @Test
    @DisplayName("Should retrieve instrument by exact symbol match")
    void testExactSymbolMatch() {
        registry.createInstrument("AAPL", "Apple", "0.01", "1", "Technology");
        registry.createInstrument("AAPLX", "Apple Extended", "0.01", "1", "Technology");

        Instrument aapl = registry.getInstrument("AAPL");
        Instrument aaplx = registry.getInstrument("AAPLX");

        assertNotEquals(aapl, aaplx);
        assertEquals("AAPL", aapl.getSymbol());
        assertEquals("AAPLX", aaplx.getSymbol());
    }

    @Test
    @DisplayName("Should maintain instruments independently")
    void testIndependentInstruments() {
        registry.createInstrument("AAPL", "Apple", "0.01", "1", "Technology");
        registry.createInstrument("GOOGL", "Google", "0.01", "1", "Technology");

        Instrument aapl = registry.getInstrument("AAPL");
        Instrument googl = registry.getInstrument("GOOGL");

        aapl.setCurrentPrice(money("150.00"));
        googl.setCurrentPrice(money("2800.00"));

        assertEquals(money("150.00"), aapl.getCurrentPrice());
        assertEquals(money("2800.00"), googl.getCurrentPrice());
    }
}

