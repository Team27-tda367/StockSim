package org.team27.stocksim.model.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

/**
 * Unit tests for Trade class.
 * Tests trade creation and getter methods.
 */
class TradeTest {
    
    private Trade trade;
    private int buyOrderId;
    private int sellOrderId;
    private String stockSymbol;
    private BigDecimal price;
    private int quantity;
    private Instant timeBeforeCreation;
    
    @BeforeEach
    void setUp() {
        buyOrderId = 1;
        sellOrderId = 2;
        stockSymbol = "AAPL";
        price = money("150.50");
        quantity = 100;
        timeBeforeCreation = Instant.now();
        
        trade = new Trade(buyOrderId, sellOrderId, stockSymbol, price, quantity, Instant.now());
    }
    
    @Test
    void testGetBuyOrderId() {
        assertEquals(buyOrderId, trade.getBuyOrderId());
    }
    
    @Test
    void testGetSellOrderId() {
        assertEquals(sellOrderId, trade.getSellOrderId());
    }
    
    @Test
    void testGetStockSymbol() {
        assertEquals(stockSymbol, trade.getStockSymbol());
    }
    
    @Test
    void testGetPrice() {
        assertEquals(price, trade.getPrice());
    }
    
    @Test
    void testGetQuantity() {
        assertEquals(quantity, trade.getQuantity());
    }
    
    @Test
    void testGetTime() {
        assertNotNull(trade.getTime());
        assertTrue(trade.getTime().isAfter(timeBeforeCreation) || trade.getTime().equals(timeBeforeCreation));
        assertTrue(trade.getTime().isBefore(Instant.now()) || trade.getTime().equals(Instant.now()));
    }
    
    @Test
    void testTradeWithSpecificTime() {
        Instant specificTime = Instant.parse("2024-01-01T12:00:00Z");
        Trade timedTrade = new Trade(1, 2, "GOOGL", money("100.00"), 50, specificTime);
        
        assertEquals(specificTime, timedTrade.getTime());
    }
    
    @Test
    void testTradeImmutability() {
        // All fields should be final, test that getters return same values
        assertEquals(buyOrderId, trade.getBuyOrderId());
        assertEquals(sellOrderId, trade.getSellOrderId());
        assertEquals(stockSymbol, trade.getStockSymbol());
        assertEquals(price, trade.getPrice());
        assertEquals(quantity, trade.getQuantity());
        
        // Call getters again to verify values haven't changed
        assertEquals(buyOrderId, trade.getBuyOrderId());
        assertEquals(sellOrderId, trade.getSellOrderId());
    }
    
    @Test
    void testTradeWithZeroQuantity() {
        Trade zeroTrade = new Trade(1, 2, "MSFT", money("100.00"), 0, Instant.now());
        assertEquals(0, zeroTrade.getQuantity());
    }
    
    @Test
    void testTradeWithLargeQuantity() {
        int largeQuantity = 1_000_000;
        Trade largeTrade = new Trade(1, 2, "TSLA", money("200.00"), largeQuantity, Instant.now());
        assertEquals(largeQuantity, largeTrade.getQuantity());
    }
    
    @Test
    void testTradeWithSmallPrice() {
        BigDecimal smallPrice = money("0.01");
        Trade smallPriceTrade = new Trade(1, 2, "PENNY", smallPrice, 100, Instant.now());
        assertEquals(smallPrice, smallPriceTrade.getPrice());
    }
    
    @Test
    void testTradeWithHighPrice() {
        BigDecimal highPrice = money("10000.00");
        Trade highPriceTrade = new Trade(1, 2, "BRK.A", highPrice, 1, Instant.now());
        assertEquals(highPrice, highPriceTrade.getPrice());
    }
    
    @Test
    void testTradeWithNegativeOrderIds() {
        // This tests current behavior - might want to add validation
        Trade negativeTrade = new Trade(-1, -2, "AAPL", money("100.00"), 10, Instant.now());
        assertEquals(-1, negativeTrade.getBuyOrderId());
        assertEquals(-2, negativeTrade.getSellOrderId());
    }
    
    @Test
    void testMultipleTradesHaveDifferentTimes() throws InterruptedException {
        Trade trade1 = new Trade(1, 2, "AAPL", money("100.00"), 10, Instant.now());
        Thread.sleep(10); // Small delay
        Trade trade2 = new Trade(3, 4, "AAPL", money("100.00"), 10, Instant.now());
        
        assertTrue(trade2.getTime().isAfter(trade1.getTime()) || trade2.getTime().equals(trade1.getTime()));
    }
    
    @Test
    void testTradeValueCalculation() {
        // Not a method in Trade, but tests how trades would be used
        BigDecimal tradeValue = trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));
        assertEquals(money("15050.00"), tradeValue);
    }
    
    @Test
    void testTradeWithDifferentSymbols() {
        Trade appleTrade = new Trade(1, 2, "AAPL", money("150.00"), 100, Instant.now());
        Trade googleTrade = new Trade(3, 4, "GOOGL", money("2800.00"), 50, Instant.now());
        
        assertNotEquals(appleTrade.getStockSymbol(), googleTrade.getStockSymbol());
        assertEquals("AAPL", appleTrade.getStockSymbol());
        assertEquals("GOOGL", googleTrade.getStockSymbol());
    }
    
    @Test
    void testTradeConstructorStoresAllParameters() {
        int buyId = 123;
        int sellId = 456;
        String symbol = "TEST";
        BigDecimal testPrice = money("99.99");
        int testQuantity = 777;
        Instant testTime = Instant.parse("2024-06-15T10:30:00Z");
        
        Trade testTrade = new Trade(buyId, sellId, symbol, testPrice, testQuantity, testTime);
        
        assertEquals(buyId, testTrade.getBuyOrderId());
        assertEquals(sellId, testTrade.getSellOrderId());
        assertEquals(symbol, testTrade.getStockSymbol());
        assertEquals(testPrice, testTrade.getPrice());
        assertEquals(testQuantity, testTrade.getQuantity());
        assertEquals(testTime, testTrade.getTime());
    }
}

