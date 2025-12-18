package org.team27.stocksim.model.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

@DisplayName("Order Tests")
class OrderTest {

    private String testSymbol;
    private String testTraderId;
    private BigDecimal testPrice;
    private int testQuantity;

    @BeforeEach
    void setUp() {
        testSymbol = "AAPL";
        testTraderId = "TRADER001";
        testPrice = money("150.00");
        testQuantity = 100;
    }

    @Test
    @DisplayName("Should create a buy order with correct properties")
    void testCreateBuyOrder() {
        Order order = new Order(Order.Side.BUY, testSymbol, testPrice, testQuantity, testTraderId);

        assertNotNull(order);
        assertTrue(order.isBuyOrder());
        assertEquals(testSymbol, order.getSymbol());
        assertEquals(testPrice, order.getPrice());
        assertEquals(testQuantity, order.getTotalQuantity());
        assertEquals(testQuantity, order.getRemainingQuantity());
        assertEquals(testTraderId, order.getTraderId());
        assertNotNull(order.getOrderId());
        assertNotNull(order.getTimeStamp());
    }

    @Test
    @DisplayName("Should create a sell order with correct properties")
    void testCreateSellOrder() {
        Order order = new Order(Order.Side.SELL, testSymbol, testPrice, testQuantity, testTraderId);

        assertNotNull(order);
        assertFalse(order.isBuyOrder());
        assertEquals(Order.Side.SELL, order.getSide());
    }

    @Test
    @DisplayName("Should generate unique order IDs")
    void testUniqueOrderIds() {
        Order order1 = new Order(Order.Side.BUY, testSymbol, testPrice, testQuantity, testTraderId);
        Order order2 = new Order(Order.Side.BUY, testSymbol, testPrice, testQuantity, testTraderId);

        assertNotEquals(order1.getOrderId(), order2.getOrderId());
    }

    @Test
    @DisplayName("Should fill order partially")
    void testPartialFill() {
        Order order = new Order(Order.Side.BUY, testSymbol, testPrice, testQuantity, testTraderId);

        order.fill(30);

        assertEquals(70, order.getRemainingQuantity());
        assertEquals(testQuantity, order.getTotalQuantity());
        assertEquals(Order.Status.PARTIALLY_FILLED, order.getStatus());
        assertFalse(order.isFilled());
    }

    @Test
    @DisplayName("Should fill order completely")
    void testCompleteFill() {
        Order order = new Order(Order.Side.BUY, testSymbol, testPrice, testQuantity, testTraderId);

        order.fill(testQuantity);

        assertEquals(0, order.getRemainingQuantity());
        assertEquals(Order.Status.FILLED, order.getStatus());
        assertTrue(order.isFilled());
    }

    @Test
    @DisplayName("Should handle multiple partial fills")
    void testMultiplePartialFills() {
        Order order = new Order(Order.Side.BUY, testSymbol, testPrice, testQuantity, testTraderId);

        order.fill(30);
        order.fill(40);
        order.fill(30);

        assertEquals(0, order.getRemainingQuantity());
        assertTrue(order.isFilled());
    }

    @Test
    @DisplayName("Should cancel order")
    void testCancelOrder() {
        Order order = new Order(Order.Side.BUY, testSymbol, testPrice, testQuantity, testTraderId);

        order.cancel();

        assertEquals(Order.Status.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("New order should have NEW status")
    void testNewOrderStatus() {
        Order order = new Order(Order.Side.BUY, testSymbol, testPrice, testQuantity, testTraderId);

        assertEquals(Order.Status.NEW, order.getStatus());
    }

    @Test
    @DisplayName("Should handle zero price orders")
    void testZeroPriceOrder() {
        Order order = new Order(Order.Side.BUY, testSymbol, BigDecimal.ZERO, testQuantity, testTraderId);

        assertEquals(BigDecimal.ZERO, order.getPrice());
    }

    @Test
    @DisplayName("Should handle large quantity orders")
    void testLargeQuantityOrder() {
        int largeQuantity = 1_000_000;
        Order order = new Order(Order.Side.BUY, testSymbol, testPrice, largeQuantity, testTraderId);

        assertEquals(largeQuantity, order.getTotalQuantity());
        assertEquals(largeQuantity, order.getRemainingQuantity());
    }

    @Test
    @DisplayName("Should maintain timestamp consistency")
    void testTimestampConsistency() {
        Order order = new Order(Order.Side.BUY, testSymbol, testPrice, testQuantity, testTraderId);

        var timestamp1 = order.getTimeStamp();
        var timestamp2 = order.getTimeStamp();

        assertEquals(timestamp1, timestamp2);
    }

    @Test
    @DisplayName("Should handle high precision prices")
    void testHighPrecisionPrice() {
        BigDecimal precisePrice = money("123.456789");
        Order order = new Order(Order.Side.BUY, testSymbol, precisePrice, testQuantity, testTraderId);

        assertEquals(precisePrice, order.getPrice());
    }
}

