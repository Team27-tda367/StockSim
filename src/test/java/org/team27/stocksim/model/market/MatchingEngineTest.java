package org.team27.stocksim.model.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

class MatchingEngineTest {
    private MatchingEngine matchingEngine;
    private OrderBook orderBook;
    private String stock;
    private String bot1 = "bot1";
    private String bot2 = "bot2";

    @BeforeEach
    void setUp() {
        matchingEngine = new MatchingEngine();
        orderBook = new OrderBook("AAPL");
        stock = new Stock("AAPL", "Apple", money("0.01"), 1).getSymbol();
    }

    @Test
    void matchExecutesSingleTradeWhenBuyPriceMeetsAskPrice() {
        orderBook.add(new Order(Order.Side.SELL, stock, 1, money("100"), 50, bot1));
        Order buyOrder = new Order(Order.Side.BUY, stock, 2, money("100"), 50, bot2);

        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(1, trades.size());
        assertTrue(buyOrder.isFilled());
        assertNull(orderBook.getBestAsk());
    }

    @Test
    void matchExecutesSingleTradeWhenBuyPriceExceedsAskPrice() {
        orderBook.add(new Order(Order.Side.SELL, stock, 1, money("95"), 30, bot1));
        Order buyOrder = new Order(Order.Side.BUY, stock, 2, money("100"), 30, bot2);

        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(1, trades.size());
        assertTrue(buyOrder.isFilled());
        assertNull(orderBook.getBestAsk());
    }

    @Test
    void matchExecutesPartialFillWhenBuyQuantityExceedsAskQuantity() {
        orderBook.add(new Order(Order.Side.SELL, stock, 1, money("100"), 30, bot1));
        Order buyOrder = new Order(Order.Side.BUY, stock, 2, money("100"), 80, bot2);

        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(1, trades.size());
        assertFalse(buyOrder.isFilled());
        assertEquals(50, buyOrder.getRemainingQuantity());
        assertEquals(buyOrder, orderBook.getBestBid());
    }

    @Test
    void matchExecutesMultipleTradesWhenBuyOrderConsumesMultipleAsks() {
        orderBook.add(new Order(Order.Side.SELL, stock, 1, money("98"), 20, bot1));
        orderBook.add(new Order(Order.Side.SELL, stock, 2, money("99"), 30, bot1));
        orderBook.add(new Order(Order.Side.SELL, stock, 3, money("100"), 40, bot1));
        Order buyOrder = new Order(Order.Side.BUY, stock, 4, money("100"), 70, bot2);

        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(3, trades.size());
        assertTrue(buyOrder.isFilled());
        assertEquals(20, orderBook.getBestAsk().getRemainingQuantity());
    }

    @Test
    void matchPlacesBuyOrderInBookWhenPriceBelowBestAsk() {
        orderBook.add(new Order(Order.Side.SELL, stock, 1, money("105"), 50, bot1));
        Order buyOrder = new Order(Order.Side.BUY, stock, 2, money("100"), 40, bot2);

        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertTrue(trades.isEmpty());
        assertEquals(buyOrder, orderBook.getBestBid());
        assertEquals(40, orderBook.getBestBid().getRemainingQuantity());
    }

    @Test
    void matchPlacesBuyOrderInBookWhenNoAsksExist() {
        Order buyOrder = new Order(Order.Side.BUY, stock, 1, money("100"), 50, bot1);

        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertTrue(trades.isEmpty());
        assertEquals(buyOrder, orderBook.getBestBid());
        assertNull(orderBook.getBestAsk());
    }

    @Test
    void matchExecutesSellOrderAgainstBestBid() {
        orderBook.add(new Order(Order.Side.BUY, stock, 1, money("100"), 60, bot1));
        Order sellOrder = new Order(Order.Side.SELL, stock, 2, money("100"), 60, bot2);

        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertEquals(1, trades.size());
        assertTrue(sellOrder.isFilled());
        assertNull(orderBook.getBestBid());
    }

    @Test
    void matchExecutesSellOrderWhenPriceBelowBestBid() {
        orderBook.add(new Order(Order.Side.BUY, stock, 1, money("100"), 50, bot1));
        Order sellOrder = new Order(Order.Side.SELL, stock, 2, money("95"), 50, bot2);

        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertEquals(1, trades.size());
        assertTrue(sellOrder.isFilled());
        assertNull(orderBook.getBestBid());
    }

    @Test
    void matchExecutesPartialSellWhenQuantityExceedsBestBid() {
        orderBook.add(new Order(Order.Side.BUY, stock, 1, money("100"), 30, bot1));
        Order sellOrder = new Order(Order.Side.SELL, stock, 2, money("100"), 70, bot2);

        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertEquals(1, trades.size());
        assertFalse(sellOrder.isFilled());
        assertEquals(40, sellOrder.getRemainingQuantity());
        assertEquals(sellOrder, orderBook.getBestAsk());
    }

    @Test
    void matchExecutesMultipleSellTradesAgainstMultipleBids() {
        orderBook.add(new Order(Order.Side.BUY, stock, 1, money("102"), 25, bot1));
        orderBook.add(new Order(Order.Side.BUY, stock, 2, money("101"), 35, bot1));
        orderBook.add(new Order(Order.Side.BUY, stock, 3, money("100"), 40, bot1));
        Order sellOrder = new Order(Order.Side.SELL, stock, 4, money("100"), 80, bot2);

        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertEquals(3, trades.size());
        assertTrue(sellOrder.isFilled());
        assertEquals(20, orderBook.getBestBid().getRemainingQuantity());
    }

    @Test
    void matchPlacesSellOrderInBookWhenPriceAboveBestBid() {
        orderBook.add(new Order(Order.Side.BUY, stock, 1, money("95"), 40, bot1));
        Order sellOrder = new Order(Order.Side.SELL, stock, 2, money("100"), 30, bot2);

        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertTrue(trades.isEmpty());
        assertEquals(sellOrder, orderBook.getBestAsk());
        assertEquals(30, orderBook.getBestAsk().getRemainingQuantity());
    }

    @Test
    void matchPlacesSellOrderInBookWhenNoBidsExist() {
        Order sellOrder = new Order(Order.Side.SELL, stock, 1, money("100"), 40, bot1);

        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertTrue(trades.isEmpty());
        assertEquals(sellOrder, orderBook.getBestAsk());
        assertNull(orderBook.getBestBid());
    }

    @Test
    void matchHandlesZeroQuantityOrdersGracefully() {
        Order buyOrder = new Order(Order.Side.BUY, stock, 1, money("100"), 0, bot1);

        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertTrue(trades.isEmpty());
        assertNull(orderBook.getBestBid());
    }

    @Test
    void matchMaintainsPriceTimePriorityForBids() {
        Order bid1 = new Order(Order.Side.BUY, stock, 1, money("100"), 20, bot1);
        Order bid2 = new Order(Order.Side.BUY, stock, 2, money("100"), 30, bot2);
        orderBook.add(bid1);
        orderBook.add(bid2);
        Order sellOrder = new Order(Order.Side.SELL, stock, 3, money("100"), 20, bot1);

        matchingEngine.match(sellOrder, orderBook);

        assertEquals(bid2, orderBook.getBestBid());
        assertEquals(30, orderBook.getBestBid().getRemainingQuantity());
    }

    @Test
    void matchMaintainsPriceTimePriorityForAsks() {
        Order ask1 = new Order(Order.Side.SELL, stock, 1, money("100"), 15, bot1);
        Order ask2 = new Order(Order.Side.SELL, stock, 2, money("100"), 25, bot2);
        orderBook.add(ask1);
        orderBook.add(ask2);
        Order buyOrder = new Order(Order.Side.BUY, stock, 3, money("100"), 15, bot1);

        matchingEngine.match(buyOrder, orderBook);

        assertEquals(ask2, orderBook.getBestAsk());
        assertEquals(25, orderBook.getBestAsk().getRemainingQuantity());
    }

    @Test
    void matchHandlesLargeBuyOrderThatExhaustsAllAsks() {
        orderBook.add(new Order(Order.Side.SELL, stock, 1, money("98"), 50, bot1));
        orderBook.add(new Order(Order.Side.SELL, stock, 2, money("99"), 40, bot1));
        orderBook.add(new Order(Order.Side.SELL, stock, 3, money("100"), 30, bot1));
        Order buyOrder = new Order(Order.Side.BUY, stock, 4, money("100"), 120, bot2);

        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertEquals(3, trades.size());
        assertTrue(buyOrder.isFilled());
        assertNull(orderBook.getBestAsk());
    }

    @Test
    void matchHandlesLargeSellOrderThatExhaustsAllBids() {
        orderBook.add(new Order(Order.Side.BUY, stock, 1, money("102"), 40, bot1));
        orderBook.add(new Order(Order.Side.BUY, stock, 2, money("101"), 35, bot1));
        orderBook.add(new Order(Order.Side.BUY, stock, 3, money("100"), 25, bot1));
        Order sellOrder = new Order(Order.Side.SELL, stock, 4, money("100"), 100, bot2);

        List<Trade> trades = matchingEngine.match(sellOrder, orderBook);

        assertEquals(3, trades.size());
        assertTrue(sellOrder.isFilled());
        assertNull(orderBook.getBestBid());
    }

    @Test
    void matchReturnsEmptyTradeListWhenNoMatchOccurs() {
        orderBook.add(new Order(Order.Side.BUY, stock, 1, money("95"), 50, bot1));
        orderBook.add(new Order(Order.Side.SELL, stock, 2, money("105"), 40, bot1));
        Order buyOrder = new Order(Order.Side.BUY, stock, 3, money("98"), 30, bot2);

        List<Trade> trades = matchingEngine.match(buyOrder, orderBook);

        assertTrue(trades.isEmpty());
        assertEquals(2, orderBook.getOrders().stream().filter(o -> o.getSide() == Order.Side.BUY).count());
    }

    @Test
    void matchHandlesAlternatingBuyAndSellOrdersCorrectly() {
        orderBook.add(new Order(Order.Side.SELL, stock, 1, money("100"), 20, bot1));
        Order buyOrder1 = new Order(Order.Side.BUY, stock, 2, money("100"), 20, bot2);
        matchingEngine.match(buyOrder1, orderBook);

        orderBook.add(new Order(Order.Side.BUY, stock, 3, money("101"), 30, bot1));
        Order sellOrder = new Order(Order.Side.SELL, stock, 4, money("101"), 30, bot2);
        matchingEngine.match(sellOrder, orderBook);

        assertNull(orderBook.getBestBid());
        assertNull(orderBook.getBestAsk());
    }
}
