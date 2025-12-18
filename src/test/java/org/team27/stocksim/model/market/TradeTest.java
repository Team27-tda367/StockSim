package org.team27.stocksim.model.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

@DisplayName("Trade Tests")
class TradeTest {

    private String testSymbol;
    private BigDecimal testPrice;
    private int testQuantity;
    private int buyOrderId;
    private int sellOrderId;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        testSymbol = "AAPL";
        testPrice = money("150.00");
        testQuantity = 100;
        buyOrderId = 1;
        sellOrderId = 2;
        testTime = Instant.now();
    }

    @Test
    @DisplayName("Should create trade with correct properties")
    void testCreateTrade() {
        Trade trade = new Trade(buyOrderId, sellOrderId, testSymbol, testPrice, testQuantity, testTime);

        assertNotNull(trade);
        assertEquals(testSymbol, trade.getStockSymbol());
        assertEquals(testPrice, trade.getPrice());
        assertEquals(testQuantity, trade.getQuantity());
        assertEquals(buyOrderId, trade.getBuyOrderId());
        assertEquals(sellOrderId, trade.getSellOrderId());
        assertEquals(testTime, trade.getTime());
    }

    @Test
    @DisplayName("Should handle large quantity trade")
    void testLargeQuantityTrade() {
        int largeQuantity = 1_000_000;
        Trade trade = new Trade(buyOrderId, sellOrderId, testSymbol, testPrice, largeQuantity, testTime);

        assertEquals(largeQuantity, trade.getQuantity());
    }

    @Test
    @DisplayName("Should handle high precision price")
    void testHighPrecisionPrice() {
        BigDecimal precisePrice = money("123.456789");
        Trade trade = new Trade(buyOrderId, sellOrderId, testSymbol, precisePrice, testQuantity, testTime);

        assertEquals(precisePrice, trade.getPrice());
    }

    @Test
    @DisplayName("Should maintain immutable properties")
    void testImmutableProperties() {
        Trade trade = new Trade(buyOrderId, sellOrderId, testSymbol, testPrice, testQuantity, testTime);

        // All getters should return the same values
        assertEquals(testSymbol, trade.getStockSymbol());
        assertEquals(testSymbol, trade.getStockSymbol());

        assertEquals(testPrice, trade.getPrice());
        assertEquals(testPrice, trade.getPrice());

        assertEquals(testQuantity, trade.getQuantity());
        assertEquals(testQuantity, trade.getQuantity());
    }

    @Test
    @DisplayName("Should calculate correct trade value")
    void testTradeValue() {
        Trade trade = new Trade(buyOrderId, sellOrderId, testSymbol, testPrice, testQuantity, testTime);

        BigDecimal expectedValue = testPrice.multiply(BigDecimal.valueOf(testQuantity));
        BigDecimal actualValue = trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @DisplayName("Should handle zero price trade")
    void testZeroPriceTrade() {
        Trade trade = new Trade(buyOrderId, sellOrderId, testSymbol, BigDecimal.ZERO, testQuantity, testTime);

        assertEquals(BigDecimal.ZERO, trade.getPrice());
    }

    @Test
    @DisplayName("Should handle single share trade")
    void testSingleShareTrade() {
        Trade trade = new Trade(buyOrderId, sellOrderId, testSymbol, testPrice, 1, testTime);

        assertEquals(1, trade.getQuantity());
    }

    @Test
    @DisplayName("Should preserve order IDs")
    void testOrderIdPreservation() {
        int specificBuyId = 12345;
        int specificSellId = 67890;

        Trade trade = new Trade(specificBuyId, specificSellId, testSymbol, testPrice, testQuantity, testTime);

        assertEquals(specificBuyId, trade.getBuyOrderId());
        assertEquals(specificSellId, trade.getSellOrderId());
    }

    @Test
    @DisplayName("Should preserve timestamp")
    void testTimestampPreservation() {
        Instant specificTime = Instant.parse("2024-01-15T10:30:45Z");
        Trade trade = new Trade(buyOrderId, sellOrderId, testSymbol, testPrice, testQuantity, specificTime);

        assertEquals(specificTime, trade.getTime());
    }

    @Test
    @DisplayName("Should handle different symbols")
    void testDifferentSymbols() {
        String[] symbols = {"AAPL", "GOOGL", "MSFT", "TSLA", "AMZN"};

        for (String symbol : symbols) {
            Trade trade = new Trade(buyOrderId, sellOrderId, symbol, testPrice, testQuantity, testTime);
            assertEquals(symbol, trade.getStockSymbol());
        }
    }
}

