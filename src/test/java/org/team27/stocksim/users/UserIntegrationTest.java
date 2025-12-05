package org.team27.stocksim.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.OrderHistory;
import org.team27.stocksim.model.users.User;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

/**
 * Integration test for a user trading scenario.
 * Tests a complete trading workflow with bots providing liquidity,
 * verifying portfolio updates, order history, and trade history.
 */
class UserIntegrationTest {

    private StockSim stockSim;
    private String userId;
    private String bot1Id;
    private String bot2Id;
    private String appleSymbol;
    private String googleSymbol;

    @BeforeEach
    void setUp() {
        stockSim = new StockSim();

        // Create stocks
        appleSymbol = "AAPL";
        googleSymbol = "GOOGL";
        stockSim.createStock(appleSymbol, "Apple Inc.", "0.01", "1");
        stockSim.createStock(googleSymbol, "Alphabet Inc.", "0.01", "1");

        // Create user
        userId = "USER1";
        stockSim.createUser(userId, "Test User");

        // Create bots to provide liquidity
        bot1Id = "BOT1";
        bot2Id = "BOT2";
        stockSim.createBot(bot1Id, "Market Maker Bot 1");
        stockSim.createBot(bot2Id, "Market Maker Bot 2");

        // Give bots initial stock positions
        stockSim.getTraders().get(bot1Id).getPortfolio().addStock(appleSymbol, 1000);
        stockSim.getTraders().get(bot1Id).getPortfolio().addStock(googleSymbol, 500);
        stockSim.getTraders().get(bot2Id).getPortfolio().addStock(appleSymbol, 500);
        stockSim.getTraders().get(bot2Id).getPortfolio().addStock(googleSymbol, 1000);
    }

    @Test
    void testUserBuysStocksFromBot() {
        // Arrange
        User user = (User) stockSim.getTraders().get(userId);
        Portfolio userPortfolio = user.getPortfolio();
        OrderHistory orderHistory = user.getOrderHistory();

        BigDecimal initialBalance = userPortfolio.getBalance();
        int initialStocks = userPortfolio.getStockQuantity(appleSymbol);

        // Bot places sell order first
        Order botSellOrder = new Order(Order.Side.SELL, appleSymbol, money("150.00"), 50, bot1Id);
        stockSim.placeOrder(botSellOrder);

        // Act: User places buy order
        Order userBuyOrder = new Order(Order.Side.BUY, appleSymbol, money("150.00"), 50, userId);
        stockSim.placeOrder(userBuyOrder);

        // Assert: Portfolio changes
        BigDecimal expectedCost = money("150.00").multiply(BigDecimal.valueOf(50));
        assertEquals(initialBalance.subtract(expectedCost), userPortfolio.getBalance(),
                "User balance should decrease by trade value");
        assertEquals(initialStocks + 50, userPortfolio.getStockQuantity(appleSymbol),
                "User should have 50 AAPL stocks");

        // Assert: Order history
        assertEquals(1, orderHistory.getAllOrders().size(), "Should have 1 order in history");
        assertEquals(userBuyOrder, orderHistory.getAllOrders().get(0), "Order should be in history");

        // Assert: Trade history
        assertEquals(1, orderHistory.getAllTrades().size(), "Should have 1 trade in history");
        assertEquals(appleSymbol, orderHistory.getAllTrades().get(0).getStockSymbol());
        assertEquals(50, orderHistory.getAllTrades().get(0).getQuantity());
        assertEquals(money("150.00"), orderHistory.getAllTrades().get(0).getPrice());
    }

    @Test
    void testUserSellsStocksToBot() {
        // Arrange
        User user = (User) stockSim.getTraders().get(userId);
        Portfolio userPortfolio = user.getPortfolio();

        // Give user some stocks to sell
        userPortfolio.addStock(appleSymbol, 100);

        BigDecimal initialBalance = userPortfolio.getBalance();
        int initialStocks = userPortfolio.getStockQuantity(appleSymbol);

        // Bot places buy order first
        Order botBuyOrder = new Order(Order.Side.BUY, appleSymbol, money("145.50"), 30, bot1Id);
        stockSim.placeOrder(botBuyOrder);

        // Act: User places sell order
        Order userSellOrder = new Order(Order.Side.SELL, appleSymbol, money("145.50"), 30, userId);
        stockSim.placeOrder(userSellOrder);

        // Assert: Portfolio changes
        BigDecimal expectedRevenue = money("145.50").multiply(BigDecimal.valueOf(30));
        assertEquals(initialBalance.add(expectedRevenue), userPortfolio.getBalance(),
                "User balance should increase by trade value");
        assertEquals(initialStocks - 30, userPortfolio.getStockQuantity(appleSymbol),
                "User should have 30 fewer AAPL stocks");

        // Assert: Trade history
        assertEquals(1, user.getOrderHistory().getAllTrades().size(), "Should have 1 trade in history");
    }

    @Test
    void testUserMultipleOrdersAndTrades() {
        // Arrange
        User user = (User) stockSim.getTraders().get(userId);
        Portfolio userPortfolio = user.getPortfolio();
        OrderHistory orderHistory = user.getOrderHistory();

        BigDecimal initialBalance = userPortfolio.getBalance();

        // Setup & Act: Place orders in sequence (bot sell, then user buy)
        // Trade 1: AAPL at 150.00
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("150.00"), 25, bot1Id));
        stockSim.placeOrder(new Order(Order.Side.BUY, appleSymbol, money("150.00"), 25, userId));

        // Trade 2: GOOGL at 280.00
        stockSim.placeOrder(new Order(Order.Side.SELL, googleSymbol, money("280.00"), 10, bot2Id));
        stockSim.placeOrder(new Order(Order.Side.BUY, googleSymbol, money("280.00"), 10, userId));

        // Trade 3: AAPL at 151.00
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("151.00"), 15, bot2Id));
        stockSim.placeOrder(new Order(Order.Side.BUY, appleSymbol, money("151.00"), 15, userId));

        // Assert: Portfolio has correct stocks
        assertEquals(40, userPortfolio.getStockQuantity(appleSymbol),
                "User should have 40 AAPL stocks (25 + 15)");
        assertEquals(10, userPortfolio.getStockQuantity(googleSymbol),
                "User should have 10 GOOGL stocks");

        // Assert: Correct balance
        BigDecimal appleTotal = money("150.00").multiply(BigDecimal.valueOf(25))
                .add(money("151.00").multiply(BigDecimal.valueOf(15)));
        BigDecimal googleTotal = money("280.00").multiply(BigDecimal.valueOf(10));
        BigDecimal expectedSpent = appleTotal.add(googleTotal);

        assertEquals(initialBalance.subtract(expectedSpent), userPortfolio.getBalance(),
                "User balance should reflect all purchases");

        // Assert: Order history contains all orders
        assertEquals(3, orderHistory.getAllOrders().size(), "Should have 3 orders in history");

        // Assert: Trade history contains all trades
        assertEquals(3, orderHistory.getAllTrades().size(), "Should have 3 trades in history");
    }

    @Test
    void testUserPartialFillScenario() {
        // Arrange
        User user = (User) stockSim.getTraders().get(userId);
        Portfolio userPortfolio = user.getPortfolio();

        BigDecimal initialBalance = userPortfolio.getBalance();

        // Bot places smaller sell order
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("150.00"), 20, bot1Id));

        // Act: User places larger buy order (only 20 will fill)
        Order userBuyOrder = new Order(Order.Side.BUY, appleSymbol, money("150.00"), 50, userId);
        stockSim.placeOrder(userBuyOrder);

        // Assert: Only partial fill occurred
        BigDecimal expectedCost = money("150.00").multiply(BigDecimal.valueOf(20));
        assertEquals(initialBalance.subtract(expectedCost), userPortfolio.getBalance(),
                "User should only pay for filled quantity");
        assertEquals(20, userPortfolio.getStockQuantity(appleSymbol),
                "User should only receive 20 stocks");

        // Assert: Order status is partially filled
        assertEquals(Order.Status.PARTIALLY_FILLED, userBuyOrder.getStatus(),
                "Order should be partially filled");
        assertEquals(30, userBuyOrder.getRemainingQuantity(),
                "30 shares should remain unfilled");
    }

    @Test
    void testUserBuyAndSellSameStock() {
        // Arrange
        User user = (User) stockSim.getTraders().get(userId);
        Portfolio userPortfolio = user.getPortfolio();
        OrderHistory orderHistory = user.getOrderHistory();

        BigDecimal initialBalance = userPortfolio.getBalance();

        // Step 1: User buys stocks
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("150.00"), 50, bot1Id));
        stockSim.placeOrder(new Order(Order.Side.BUY, appleSymbol, money("150.00"), 50, userId));

        BigDecimal balanceAfterBuy = userPortfolio.getBalance();
        int stocksAfterBuy = userPortfolio.getStockQuantity(appleSymbol);

        // Step 2: User sells some stocks at higher price
        stockSim.placeOrder(new Order(Order.Side.BUY, appleSymbol, money("155.00"), 30, bot2Id));
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("155.00"), 30, userId));

        // Assert: Portfolio changes
        BigDecimal expectedRevenue = money("155.00").multiply(BigDecimal.valueOf(30));
        assertEquals(balanceAfterBuy.add(expectedRevenue), userPortfolio.getBalance(),
                "User balance should increase from sale");
        assertEquals(stocksAfterBuy - 30, userPortfolio.getStockQuantity(appleSymbol),
                "User should have 20 stocks remaining");

        // Assert: Profit calculation
        BigDecimal buyPrice = money("150.00").multiply(BigDecimal.valueOf(50));
        BigDecimal sellRevenue = money("155.00").multiply(BigDecimal.valueOf(30));
        BigDecimal netCost = buyPrice.subtract(sellRevenue);

        assertEquals(initialBalance.subtract(netCost), userPortfolio.getBalance(),
                "User made profit on the trade");

        // Assert: Order and trade history
        assertEquals(2, orderHistory.getAllOrders().size(), "Should have 2 orders (buy and sell)");
        assertEquals(2, orderHistory.getAllTrades().size(), "Should have 2 trades");
    }

    @Test
    void testUserNoMatchingOrders() {
        // Arrange
        User user = (User) stockSim.getTraders().get(userId);
        Portfolio userPortfolio = user.getPortfolio();
        OrderHistory orderHistory = user.getOrderHistory();

        BigDecimal initialBalance = userPortfolio.getBalance();
        int initialStocks = userPortfolio.getStockQuantity(appleSymbol);

        // Bot places sell order at higher price
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("150.00"), 50, bot1Id));

        // Act: User places buy order at lower price (no match)
        Order userBuyOrder = new Order(Order.Side.BUY, appleSymbol, money("145.00"), 50, userId);
        stockSim.placeOrder(userBuyOrder);

        // Assert: No trade occurred
        assertEquals(initialBalance, userPortfolio.getBalance(),
                "User balance should be unchanged");
        assertEquals(initialStocks, userPortfolio.getStockQuantity(appleSymbol),
                "User stock quantity should be unchanged");

        // Assert: Order is in history but no trades
        assertEquals(1, orderHistory.getAllOrders().size(), "Order should be in history");
        assertEquals(0, orderHistory.getAllTrades().size(), "No trades should have occurred");

        // Assert: Order is still NEW (not filled)
        assertEquals(Order.Status.NEW, userBuyOrder.getStatus(), "Order should remain unfilled");
    }

    @Test
    void testUserMultipleTradesFromOneOrder() {
        // Arrange
        User user = (User) stockSim.getTraders().get(userId);
        Portfolio userPortfolio = user.getPortfolio();
        OrderHistory orderHistory = user.getOrderHistory();

        // Multiple bots place small sell orders
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("150.00"), 15, bot1Id));
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("150.00"), 20, bot2Id));
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("150.00"), 15, bot1Id));

        BigDecimal initialBalance = userPortfolio.getBalance();

        // Act: User places one large buy order that matches all
        Order userBuyOrder = new Order(Order.Side.BUY, appleSymbol, money("150.00"), 50, userId);
        stockSim.placeOrder(userBuyOrder);

        // Assert: All trades completed
        assertEquals(50, userPortfolio.getStockQuantity(appleSymbol),
                "User should have all 50 stocks");

        BigDecimal expectedCost = money("150.00").multiply(BigDecimal.valueOf(50));
        assertEquals(initialBalance.subtract(expectedCost), userPortfolio.getBalance(),
                "User balance should reflect total purchase");

        // Assert: Multiple trades from single order
        assertEquals(1, orderHistory.getAllOrders().size(), "Should have 1 order");
        assertEquals(3, orderHistory.getAllTrades().size(), "Should have 3 separate trades");

        // Assert: Order is fully filled
        assertEquals(Order.Status.FILLED, userBuyOrder.getStatus(), "Order should be fully filled");
        assertEquals(0, userBuyOrder.getRemainingQuantity(), "No quantity should remain");
    }

    @Test
    void testUserPortfolioWithMultipleStocks() {
        // Arrange
        User user = (User) stockSim.getTraders().get(userId);
        Portfolio userPortfolio = user.getPortfolio();

        // Act: Place orders for AAPL
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("150.00"), 30, bot1Id));
        stockSim.placeOrder(new Order(Order.Side.BUY, appleSymbol, money("150.00"), 30, userId));

        // Place orders for GOOGL
        stockSim.placeOrder(new Order(Order.Side.SELL, googleSymbol, money("280.00"), 5, bot2Id));
        stockSim.placeOrder(new Order(Order.Side.BUY, googleSymbol, money("280.00"), 5, userId));

        // Assert: Portfolio contains both stocks
        assertEquals(30, userPortfolio.getStockQuantity(appleSymbol), "Should have 30 AAPL");
        assertEquals(5, userPortfolio.getStockQuantity(googleSymbol), "Should have 5 GOOGL");
        assertFalse(userPortfolio.isEmpty(), "Portfolio should not be empty");

        // Assert: Stock holdings map
        var holdings = userPortfolio.getStockHoldings();
        assertEquals(2, holdings.size(), "Should have holdings in 2 different stocks");
        assertTrue(holdings.containsKey(appleSymbol), "Should contain AAPL");
        assertTrue(holdings.containsKey(googleSymbol), "Should contain GOOGL");
    }

    @Test
    void testUserOrderHistoryTracking() {
        // Arrange
        User user = (User) stockSim.getTraders().get(userId);
        OrderHistory orderHistory = user.getOrderHistory();

        // Setup bot orders
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("150.00"), 10, bot1Id));
        stockSim.placeOrder(new Order(Order.Side.SELL, appleSymbol, money("151.00"), 10, bot1Id));

        // Act: User places multiple orders
        Order order1 = new Order(Order.Side.BUY, appleSymbol, money("150.00"), 10, userId);
        Order order2 = new Order(Order.Side.BUY, appleSymbol, money("151.00"), 10, userId);
        Order order3 = new Order(Order.Side.BUY, googleSymbol, money("280.00"), 5, userId); // Won't match

        stockSim.placeOrder(order1);
        stockSim.placeOrder(order2);
        stockSim.placeOrder(order3);

        // Assert: All orders tracked
        assertEquals(3, orderHistory.getAllOrders().size(), "Should track all 3 orders");
        assertTrue(orderHistory.getAllOrders().contains(order1), "Should contain first order");
        assertTrue(orderHistory.getAllOrders().contains(order2), "Should contain second order");
        assertTrue(orderHistory.getAllOrders().contains(order3), "Should contain third order");

        // Assert: Only matched orders result in trades
        assertEquals(2, orderHistory.getAllTrades().size(),
                "Only 2 orders matched, so 2 trades");
    }
}

