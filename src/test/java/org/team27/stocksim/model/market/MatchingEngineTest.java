package org.team27.stocksim.model.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

@DisplayName("MatchingEngine Tests")
class MatchingEngineTest {

    private MatchingEngine matchingEngine;
    private OrderBook orderBook;
    private String testSymbol;

    @BeforeEach
    void setUp() {
        matchingEngine = new MatchingEngine();
        testSymbol = "AAPL";
        orderBook = new OrderBook(testSymbol);
    }

    @Test
    @DisplayName("Should match buy order with existing sell order at same price")
    void testMatchBuyWithSell() {
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "SELLER");
        orderBook.add(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "BUYER");
        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(testSymbol, trade.getStockSymbol());
        assertEquals(money("150.00"), trade.getPrice());
        assertEquals(100, trade.getQuantity());
        assertTrue(buyOrder.isFilled());
        assertTrue(sellOrder.isFilled());
    }

    @Test
    @DisplayName("Should match sell order with existing buy order at same price")
    void testMatchSellWithBuy() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "BUYER");
        orderBook.add(buyOrder);

        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "SELLER");
        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(100, trade.getQuantity());
        assertTrue(buyOrder.isFilled());
        assertTrue(sellOrder.isFilled());
    }

    @Test
    @DisplayName("Should match buy order at higher price than best ask")
    void testMatchBuyAtHigherPrice() {
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "SELLER");
        orderBook.add(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("152.00"), 100, "BUYER");
        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(1, trades.size());
        // Should execute at the ask price (150.00)
        assertEquals(money("150.00"), trades.get(0).getPrice());
    }

    @Test
    @DisplayName("Should match sell order at lower price than best bid")
    void testMatchSellAtLowerPrice() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "BUYER");
        orderBook.add(buyOrder);

        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("148.00"), 100, "SELLER");
        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertEquals(1, trades.size());
        // Should execute at the bid price (150.00)
        assertEquals(money("150.00"), trades.get(0).getPrice());
    }

    @Test
    @DisplayName("Should not match buy order below best ask")
    void testNoMatchBuyBelowAsk() {
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "SELLER");
        orderBook.add(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("149.00"), 100, "BUYER");
        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(0, trades.size());
        assertFalse(buyOrder.isFilled());
        assertEquals(100, buyOrder.getRemainingQuantity());
        // Order should be added to book
        assertNotNull(orderBook.getBestBid());
    }

    @Test
    @DisplayName("Should not match sell order above best bid")
    void testNoMatchSellAboveBid() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "BUYER");
        orderBook.add(buyOrder);

        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("151.00"), 100, "SELLER");
        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertEquals(0, trades.size());
        assertFalse(sellOrder.isFilled());
        assertEquals(100, sellOrder.getRemainingQuantity());
        // Order should be added to book
        assertNotNull(orderBook.getBestAsk());
    }

    @Test
    @DisplayName("Should partially fill buy order with smaller sell order")
    void testPartialFillBuyOrder() {
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 50, "SELLER");
        orderBook.add(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "BUYER");
        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(1, trades.size());
        assertEquals(50, trades.get(0).getQuantity());
        assertFalse(buyOrder.isFilled());
        assertEquals(50, buyOrder.getRemainingQuantity());
        assertTrue(sellOrder.isFilled());
    }

    @Test
    @DisplayName("Should partially fill sell order with smaller buy order")
    void testPartialFillSellOrder() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 50, "BUYER");
        orderBook.add(buyOrder);

        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "SELLER");
        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertEquals(1, trades.size());
        assertEquals(50, trades.get(0).getQuantity());
        assertFalse(sellOrder.isFilled());
        assertEquals(50, sellOrder.getRemainingQuantity());
        assertTrue(buyOrder.isFilled());
    }

    @Test
    @DisplayName("Should match with multiple orders at different price levels")
    void testMatchMultiplePriceLevels() {
        // Add three sell orders at different prices
        orderBook.add(new Order(Order.Side.SELL, testSymbol, money("150.00"), 30, "SELLER1"));
        orderBook.add(new Order(Order.Side.SELL, testSymbol, money("151.00"), 30, "SELLER2"));
        orderBook.add(new Order(Order.Side.SELL, testSymbol, money("152.00"), 30, "SELLER3"));

        // Buy order that can match all three
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("152.00"), 90, "BUYER");
        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(3, trades.size());
        assertEquals(money("150.00"), trades.get(0).getPrice());
        assertEquals(money("151.00"), trades.get(1).getPrice());
        assertEquals(money("152.00"), trades.get(2).getPrice());
        assertTrue(buyOrder.isFilled());
    }

    @Test
    @DisplayName("Should match with multiple orders at same price level")
    void testMatchMultipleSamePrice() {
        // Add two sell orders at same price
        orderBook.add(new Order(Order.Side.SELL, testSymbol, money("150.00"), 40, "SELLER1"));
        orderBook.add(new Order(Order.Side.SELL, testSymbol, money("150.00"), 40, "SELLER2"));

        // Buy order that matches both
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 80, "BUYER");
        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(2, trades.size());
        assertEquals(40, trades.get(0).getQuantity());
        assertEquals(40, trades.get(1).getQuantity());
        assertTrue(buyOrder.isFilled());
    }

    @Test
    @DisplayName("Should remove filled orders from book")
    void testRemoveFilledOrders() {
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "SELLER");
        orderBook.add(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "BUYER");
        matchingEngine.match(buyOrder, orderBook);

        // Sell order should be removed from book since it's filled
        assertNull(orderBook.getBestAsk());
    }

    @Test
    @DisplayName("Should keep partially filled orders in book")
    void testKeepPartiallyFilledOrders() {
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "SELLER");
        orderBook.add(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 50, "BUYER");
        matchingEngine.match(buyOrder, orderBook);

        // Sell order should remain in book with reduced quantity
        assertNotNull(orderBook.getBestAsk());
        assertEquals(50, orderBook.getBestAsk().getRemainingQuantity());
    }

    @Test
    @DisplayName("Should add unmatched order to book")
    void testAddUnmatchedOrderToBook() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "BUYER");
        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(0, trades.size());
        assertNotNull(orderBook.getBestBid());
        assertEquals(buyOrder, orderBook.getBestBid());
    }

    @Test
    @DisplayName("Should handle zero quantity match")
    void testZeroQuantityMatch() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 0, "BUYER");
        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(0, trades.size());
    }

    @Test
    @DisplayName("Should preserve order IDs in trades")
    void testPreserveOrderIds() {
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "SELLER");
        orderBook.add(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "BUYER");
        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(buyOrder.getOrderId(), trade.getBuyOrderId());
        assertEquals(sellOrder.getOrderId(), trade.getSellOrderId());
    }

    @Test
    @DisplayName("Should create trade with timestamp")
    void testTradeTimestamp() {
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "SELLER");
        orderBook.add(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "BUYER");
        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(1, trades.size());
        assertNotNull(trades.get(0).getTime());
    }
}

