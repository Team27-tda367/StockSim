package org.team27.stocksim.model.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

@DisplayName("OrderBook Tests")
class OrderBookTest {

    private OrderBook orderBook;
    private String testSymbol;

    @BeforeEach
    void setUp() {
        testSymbol = "AAPL";
        orderBook = new OrderBook(testSymbol);
    }

    @Test
    @DisplayName("Should create empty order book")
    void testCreateOrderBook() {
        assertNotNull(orderBook);
        assertNull(orderBook.getBestBid());
        assertNull(orderBook.getBestAsk());
    }

    @Test
    @DisplayName("Should add buy order to bids")
    void testAddBuyOrder() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "TRADER001");

        orderBook.add(buyOrder);

        assertEquals(buyOrder, orderBook.getBestBid());
        assertNull(orderBook.getBestAsk());
    }

    @Test
    @DisplayName("Should add sell order to asks")
    void testAddSellOrder() {
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "TRADER001");

        orderBook.add(sellOrder);

        assertEquals(sellOrder, orderBook.getBestAsk());
        assertNull(orderBook.getBestBid());
    }

    @Test
    @DisplayName("Should prioritize highest bid")
    void testBestBidPriority() {
        Order buyOrder1 = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "TRADER001");
        Order buyOrder2 = new Order(Order.Side.BUY, testSymbol, money("151.00"), 100, "TRADER002");
        Order buyOrder3 = new Order(Order.Side.BUY, testSymbol, money("149.00"), 100, "TRADER003");

        orderBook.add(buyOrder1);
        orderBook.add(buyOrder2);
        orderBook.add(buyOrder3);

        assertEquals(buyOrder2, orderBook.getBestBid());
        assertEquals(money("151.00"), orderBook.getBestBid().getPrice());
    }

    @Test
    @DisplayName("Should prioritize lowest ask")
    void testBestAskPriority() {
        Order sellOrder1 = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "TRADER001");
        Order sellOrder2 = new Order(Order.Side.SELL, testSymbol, money("151.00"), 100, "TRADER002");
        Order sellOrder3 = new Order(Order.Side.SELL, testSymbol, money("149.00"), 100, "TRADER003");

        orderBook.add(sellOrder1);
        orderBook.add(sellOrder2);
        orderBook.add(sellOrder3);

        assertEquals(sellOrder3, orderBook.getBestAsk());
        assertEquals(money("149.00"), orderBook.getBestAsk().getPrice());
    }

    @Test
    @DisplayName("Should prioritize by time for same price bids")
    void testTimePriorityForBids() throws InterruptedException {
        Order buyOrder1 = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "TRADER001");
        Thread.sleep(10); // Ensure different timestamps
        Order buyOrder2 = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "TRADER002");

        orderBook.add(buyOrder1);
        orderBook.add(buyOrder2);

        assertEquals(buyOrder1, orderBook.getBestBid());
    }

    @Test
    @DisplayName("Should prioritize by time for same price asks")
    void testTimePriorityForAsks() throws InterruptedException {
        Order sellOrder1 = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "TRADER001");
        Thread.sleep(10); // Ensure different timestamps
        Order sellOrder2 = new Order(Order.Side.SELL, testSymbol, money("150.00"), 100, "TRADER002");

        orderBook.add(sellOrder1);
        orderBook.add(sellOrder2);

        assertEquals(sellOrder1, orderBook.getBestAsk());
    }

    @Test
    @DisplayName("Should remove order from book")
    void testRemoveOrder() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "TRADER001");

        orderBook.add(buyOrder);
        assertEquals(buyOrder, orderBook.getBestBid());

        orderBook.remove(buyOrder);
        assertNull(orderBook.getBestBid());
    }

    @Test
    @DisplayName("Should update best bid after removal")
    void testBestBidAfterRemoval() {
        Order buyOrder1 = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "TRADER001");
        Order buyOrder2 = new Order(Order.Side.BUY, testSymbol, money("151.00"), 100, "TRADER002");

        orderBook.add(buyOrder1);
        orderBook.add(buyOrder2);

        orderBook.remove(buyOrder2);
        assertEquals(buyOrder1, orderBook.getBestBid());
    }

    @Test
    @DisplayName("Should get all orders from book")
    void testGetAllOrders() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "TRADER001");
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("151.00"), 100, "TRADER002");

        orderBook.add(buyOrder);
        orderBook.add(sellOrder);

        ArrayList<Order> allOrders = orderBook.getOrders();
        assertEquals(2, allOrders.size());
        assertTrue(allOrders.contains(buyOrder));
        assertTrue(allOrders.contains(sellOrder));
    }

    @Test
    @DisplayName("Should fill order and update quantity")
    void testFillOrder() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "TRADER001");

        orderBook.fillOrder(buyOrder, 30);

        assertEquals(70, buyOrder.getRemainingQuantity());
    }

    @Test
    @DisplayName("Should handle multiple orders at different price levels")
    void testMultiplePriceLevels() {
        // Add bids at different levels
        orderBook.add(new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "TRADER001"));
        orderBook.add(new Order(Order.Side.BUY, testSymbol, money("149.00"), 100, "TRADER002"));
        orderBook.add(new Order(Order.Side.BUY, testSymbol, money("148.00"), 100, "TRADER003"));

        // Add asks at different levels
        orderBook.add(new Order(Order.Side.SELL, testSymbol, money("151.00"), 100, "TRADER004"));
        orderBook.add(new Order(Order.Side.SELL, testSymbol, money("152.00"), 100, "TRADER005"));
        orderBook.add(new Order(Order.Side.SELL, testSymbol, money("153.00"), 100, "TRADER006"));

        assertEquals(money("150.00"), orderBook.getBestBid().getPrice());
        assertEquals(money("151.00"), orderBook.getBestAsk().getPrice());
        assertEquals(6, orderBook.getOrders().size());
    }

    @Test
    @DisplayName("Should maintain spread between bid and ask")
    void testBidAskSpread() {
        Order buyOrder = new Order(Order.Side.BUY, testSymbol, money("150.00"), 100, "TRADER001");
        Order sellOrder = new Order(Order.Side.SELL, testSymbol, money("151.00"), 100, "TRADER002");

        orderBook.add(buyOrder);
        orderBook.add(sellOrder);

        BigDecimal spread = orderBook.getBestAsk().getPrice().subtract(orderBook.getBestBid().getPrice());
        assertEquals(money("1.00"), spread);
    }
}

