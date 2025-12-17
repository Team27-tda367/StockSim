package org.team27.stocksim.model.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.market.Trade;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

@DisplayName("OrderHistory Tests")
class OrderHistoryTest {

    private OrderHistory orderHistory;

    @BeforeEach
    void setUp() {
        orderHistory = new OrderHistory();
    }

    @Test
    @DisplayName("Should create empty order history")
    void testCreateEmptyOrderHistory() {
        assertNotNull(orderHistory);
        assertTrue(orderHistory.getAllOrders().isEmpty());
        assertTrue(orderHistory.getAllTrades().isEmpty());
    }

    @Test
    @DisplayName("Should add order to history")
    void testAddOrder() {
        Order order = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "TRADER001");

        orderHistory.addOrder(order);

        List<Order> orders = orderHistory.getAllOrders();
        assertEquals(1, orders.size());
        assertTrue(orders.contains(order));
    }

    @Test
    @DisplayName("Should add multiple orders to history")
    void testAddMultipleOrders() {
        Order order1 = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "TRADER001");
        Order order2 = new Order(Order.Side.SELL, "GOOGL", money("2800.00"), 50, "TRADER001");
        Order order3 = new Order(Order.Side.BUY, "MSFT", money("380.00"), 75, "TRADER001");

        orderHistory.addOrder(order1);
        orderHistory.addOrder(order2);
        orderHistory.addOrder(order3);

        assertEquals(3, orderHistory.getAllOrders().size());
    }

    @Test
    @DisplayName("Should add trade to history")
    void testAddTrade() {
        Trade trade = new Trade(1, 2, "AAPL", money("150.00"), 100, Instant.now());

        orderHistory.addTrade(trade);

        List<Trade> trades = orderHistory.getAllTrades();
        assertEquals(1, trades.size());
        assertTrue(trades.contains(trade));
    }

    @Test
    @DisplayName("Should add multiple trades to history")
    void testAddMultipleTrades() {
        Trade trade1 = new Trade(1, 2, "AAPL", money("150.00"), 100, Instant.now());
        Trade trade2 = new Trade(3, 4, "GOOGL", money("2800.00"), 50, Instant.now());
        Trade trade3 = new Trade(5, 6, "MSFT", money("380.00"), 75, Instant.now());

        orderHistory.addTrade(trade1);
        orderHistory.addTrade(trade2);
        orderHistory.addTrade(trade3);

        assertEquals(3, orderHistory.getAllTrades().size());
    }

    @Test
    @DisplayName("Should get orders by symbol")
    void testGetOrdersBySymbol() {
        Order order1 = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "TRADER001");
        Order order2 = new Order(Order.Side.SELL, "GOOGL", money("2800.00"), 50, "TRADER001");
        Order order3 = new Order(Order.Side.BUY, "AAPL", money("155.00"), 75, "TRADER001");

        orderHistory.addOrder(order1);
        orderHistory.addOrder(order2);
        orderHistory.addOrder(order3);

        List<Order> aaplOrders = orderHistory.getOrdersBySymbol("AAPL");
        assertEquals(2, aaplOrders.size());
        assertTrue(aaplOrders.contains(order1));
        assertTrue(aaplOrders.contains(order3));
    }

    @Test
    @DisplayName("Should get trades by symbol")
    void testGetTradesBySymbol() {
        Trade trade1 = new Trade(1, 2, "AAPL", money("150.00"), 100, Instant.now());
        Trade trade2 = new Trade(3, 4, "GOOGL", money("2800.00"), 50, Instant.now());
        Trade trade3 = new Trade(5, 6, "AAPL", money("155.00"), 75, Instant.now());

        orderHistory.addTrade(trade1);
        orderHistory.addTrade(trade2);
        orderHistory.addTrade(trade3);

        List<Trade> aaplTrades = orderHistory.getTradesBySymbol("AAPL");
        assertEquals(2, aaplTrades.size());
        assertTrue(aaplTrades.contains(trade1));
        assertTrue(aaplTrades.contains(trade3));
    }

    @Test
    @DisplayName("Should return empty list for non-existent symbol orders")
    void testGetOrdersByNonExistentSymbol() {
        Order order = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "TRADER001");
        orderHistory.addOrder(order);

        List<Order> orders = orderHistory.getOrdersBySymbol("GOOGL");
        assertTrue(orders.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for non-existent symbol trades")
    void testGetTradesByNonExistentSymbol() {
        Trade trade = new Trade(1, 2, "AAPL", money("150.00"), 100, Instant.now());
        orderHistory.addTrade(trade);

        List<Trade> trades = orderHistory.getTradesBySymbol("GOOGL");
        assertTrue(trades.isEmpty());
    }

    @Test
    @DisplayName("Should return unmodifiable list of orders")
    void testGetAllOrdersReturnsUnmodifiable() {
        Order order = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "TRADER001");
        orderHistory.addOrder(order);

        List<Order> orders = orderHistory.getAllOrders();

        assertThrows(UnsupportedOperationException.class, () -> {
            orders.add(new Order(Order.Side.SELL, "GOOGL", money("2800.00"), 50, "TRADER001"));
        });
    }

    @Test
    @DisplayName("Should return unmodifiable list of trades")
    void testGetAllTradesReturnsUnmodifiable() {
        Trade trade = new Trade(1, 2, "AAPL", money("150.00"), 100, Instant.now());
        orderHistory.addTrade(trade);

        List<Trade> trades = orderHistory.getAllTrades();

        assertThrows(UnsupportedOperationException.class, () -> {
            trades.add(new Trade(3, 4, "GOOGL", money("2800.00"), 50, Instant.now()));
        });
    }

    @Test
    @DisplayName("Should maintain order insertion sequence")
    void testOrderInsertionSequence() {
        Order order1 = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "TRADER001");
        Order order2 = new Order(Order.Side.SELL, "GOOGL", money("2800.00"), 50, "TRADER001");
        Order order3 = new Order(Order.Side.BUY, "MSFT", money("380.00"), 75, "TRADER001");

        orderHistory.addOrder(order1);
        orderHistory.addOrder(order2);
        orderHistory.addOrder(order3);

        List<Order> orders = orderHistory.getAllOrders();
        assertEquals(order1, orders.get(0));
        assertEquals(order2, orders.get(1));
        assertEquals(order3, orders.get(2));
    }

    @Test
    @DisplayName("Should maintain trade insertion sequence")
    void testTradeInsertionSequence() {
        Trade trade1 = new Trade(1, 2, "AAPL", money("150.00"), 100, Instant.now());
        Trade trade2 = new Trade(3, 4, "GOOGL", money("2800.00"), 50, Instant.now());
        Trade trade3 = new Trade(5, 6, "MSFT", money("380.00"), 75, Instant.now());

        orderHistory.addTrade(trade1);
        orderHistory.addTrade(trade2);
        orderHistory.addTrade(trade3);

        List<Trade> trades = orderHistory.getAllTrades();
        assertEquals(trade1, trades.get(0));
        assertEquals(trade2, trades.get(1));
        assertEquals(trade3, trades.get(2));
    }

    @Test
    @DisplayName("Should handle many orders")
    void testManyOrders() {
        for (int i = 0; i < 1000; i++) {
            Order order = new Order(Order.Side.BUY, "STOCK" + i, money("100.00"), 10, "TRADER001");
            orderHistory.addOrder(order);
        }

        assertEquals(1000, orderHistory.getAllOrders().size());
    }

    @Test
    @DisplayName("Should handle many trades")
    void testManyTrades() {
        for (int i = 0; i < 1000; i++) {
            Trade trade = new Trade(i * 2, i * 2 + 1, "STOCK" + i, money("100.00"), 10, Instant.now());
            orderHistory.addTrade(trade);
        }

        assertEquals(1000, orderHistory.getAllTrades().size());
    }

    @Test
    @DisplayName("Should track both buy and sell orders separately")
    void testBuyAndSellOrders() {
        Order buyOrder = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "TRADER001");
        Order sellOrder = new Order(Order.Side.SELL, "AAPL", money("155.00"), 100, "TRADER001");

        orderHistory.addOrder(buyOrder);
        orderHistory.addOrder(sellOrder);

        List<Order> aaplOrders = orderHistory.getOrdersBySymbol("AAPL");
        assertEquals(2, aaplOrders.size());
    }

    @Test
    @DisplayName("Should handle orders and trades independently")
    void testIndependentOrdersAndTrades() {
        Order order = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "TRADER001");
        Trade trade = new Trade(1, 2, "AAPL", money("150.00"), 100, Instant.now());

        orderHistory.addOrder(order);
        orderHistory.addTrade(trade);

        assertEquals(1, orderHistory.getAllOrders().size());
        assertEquals(1, orderHistory.getAllTrades().size());
    }

    @Test
    @DisplayName("Should filter orders by exact symbol match")
    void testExactSymbolMatchOrders() {
        orderHistory.addOrder(new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "TRADER001"));
        orderHistory.addOrder(new Order(Order.Side.BUY, "AAPLX", money("150.00"), 100, "TRADER001"));

        List<Order> aaplOrders = orderHistory.getOrdersBySymbol("AAPL");
        assertEquals(1, aaplOrders.size());
        assertEquals("AAPL", aaplOrders.get(0).getSymbol());
    }

    @Test
    @DisplayName("Should filter trades by exact symbol match")
    void testExactSymbolMatchTrades() {
        orderHistory.addTrade(new Trade(1, 2, "AAPL", money("150.00"), 100, Instant.now()));
        orderHistory.addTrade(new Trade(3, 4, "AAPLX", money("150.00"), 100, Instant.now()));

        List<Trade> aaplTrades = orderHistory.getTradesBySymbol("AAPL");
        assertEquals(1, aaplTrades.size());
        assertEquals("AAPL", aaplTrades.get(0).getStockSymbol());
    }
}

