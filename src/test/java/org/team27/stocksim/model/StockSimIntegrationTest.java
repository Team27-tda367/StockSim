package org.team27.stocksim.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.market.Trade;
import org.team27.stocksim.model.util.dto.InstrumentDTO;
import org.team27.stocksim.model.users.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

@DisplayName("StockSim Integration Tests")
class StockSimIntegrationTest {

    private StockSim stockSim;

    @BeforeEach
    void setUp() {
        stockSim = new StockSim();
    }

    @Test
    @DisplayName("Should initialize StockSim successfully")
    void testInitialization() {
        assertNotNull(stockSim);
    }

    @Test
    @DisplayName("Should create and retrieve stock")
    void testCreateAndRetrieveStock() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");

        var stock = stockSim.getStocks().get("AAPL");
        assertNotNull(stock);
        assertEquals("AAPL", stock.getSymbol());
        assertEquals("Apple Inc.", stock.getName());
    }

    @Test
    @DisplayName("Should create and retrieve user")
    void testCreateAndRetrieveUser() {
        stockSim.createUser("USER001", "Test User");

        User user = (User) stockSim.getTraders().get("USER001");
        assertNotNull(user);
        assertEquals("USER001", user.getId());
        assertEquals("Test User", user.getDisplayName());
    }

    @Test
    @DisplayName("Should create bot")
    void testCreateBot() {
        stockSim.createBot("BOT001", "Test Bot");

        assertNotNull(stockSim.getTraders().get("BOT001"));
    }

    @Test
    @DisplayName("Should place buy order")
    void testPlaceBuyOrder() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createUser("USER001", "Test User");

        Order order = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "USER001");
        stockSim.placeOrder(order);

        // Order should be in the order book
        assertNotNull(stockSim.getOrderBook("AAPL"));
    }

    @Test
    @DisplayName("Should match buy and sell orders")
    void testMatchOrders() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createUser("BUYER", "Buyer User");
        stockSim.createUser("SELLER", "Seller User");

        // Give seller some stocks
        User seller = (User) stockSim.getTraders().get("SELLER");
        seller.getPortfolio().addStock("AAPL", 100);

        // Place sell order first
        Order sellOrder = new Order(Order.Side.SELL, "AAPL", money("150.00"), 100, "SELLER");
        stockSim.placeOrder(sellOrder);

        // Place matching buy order
        Order buyOrder = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "BUYER");
        stockSim.placeOrder(buyOrder);

        // Both orders should be filled
        assertTrue(buyOrder.isFilled());
        assertTrue(sellOrder.isFilled());
    }

    @Test
    @DisplayName("Should update stock price after trade")
    void testStockPriceUpdate() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createUser("BUYER", "Buyer User");
        stockSim.createUser("SELLER", "Seller User");

        User seller = (User) stockSim.getTraders().get("SELLER");
        seller.getPortfolio().addStock("AAPL", 100);

        BigDecimal tradePrice = money("50.00"); // Price buyer can afford: 50*100=5000

        Order sellOrder = new Order(Order.Side.SELL, "AAPL", tradePrice, 100, "SELLER");
        stockSim.placeOrder(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, "AAPL", tradePrice, 100, "BUYER");
        stockSim.placeOrder(buyOrder);

        // Stock price should be updated to trade price
        var stock = stockSim.getStocks().get("AAPL");
        assertEquals(tradePrice, stock.getPrice());
    }

    @Test
    @DisplayName("Should settle trade and update portfolios")
    void testTradeSettlement() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createUser("BUYER", "Buyer User");
        stockSim.createUser("SELLER", "Seller User");

        User buyer = (User) stockSim.getTraders().get("BUYER");
        User seller = (User) stockSim.getTraders().get("SELLER");

        BigDecimal initialBuyerBalance = buyer.getPortfolio().getBalance();
        BigDecimal initialSellerBalance = seller.getPortfolio().getBalance();

        seller.getPortfolio().addStock("AAPL", 100);

        BigDecimal tradePrice = money("50.00"); // Affordable: 50*100=5000 < 10000
        int quantity = 100;

        Order sellOrder = new Order(Order.Side.SELL, "AAPL", tradePrice, quantity, "SELLER");
        stockSim.placeOrder(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, "AAPL", tradePrice, quantity, "BUYER");
        stockSim.placeOrder(buyOrder);

        // Buyer should have less cash and more stock
        BigDecimal tradeCost = tradePrice.multiply(BigDecimal.valueOf(quantity));
        assertEquals(initialBuyerBalance.subtract(tradeCost), buyer.getPortfolio().getBalance());
        assertEquals(quantity, buyer.getPortfolio().getStockQuantity("AAPL"));

        // Seller should have more cash and less stock
        assertEquals(initialSellerBalance.add(tradeCost), seller.getPortfolio().getBalance());
        assertEquals(0, seller.getPortfolio().getStockQuantity("AAPL"));
    }

    @Test
    @DisplayName("Should track order history for users")
    void testOrderHistory() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createUser("USER001", "Test User");

        User user = (User) stockSim.getTraders().get("USER001");

        Order order = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "USER001");
        stockSim.placeOrder(order);

        List<Order> orders = user.getOrderHistory().getAllOrders();
        assertEquals(1, orders.size());
        assertTrue(orders.contains(order));
    }

    @Test
    @DisplayName("Should track trade history for users")
    void testTradeHistory() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createUser("BUYER", "Buyer User");
        stockSim.createUser("SELLER", "Seller User");

        User buyer = (User) stockSim.getTraders().get("BUYER");
        User seller = (User) stockSim.getTraders().get("SELLER");

        seller.getPortfolio().addStock("AAPL", 100);

        Order sellOrder = new Order(Order.Side.SELL, "AAPL", money("50.00"), 100, "SELLER");
        stockSim.placeOrder(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, "AAPL", money("50.00"), 100, "BUYER");
        stockSim.placeOrder(buyOrder);

        // Both users should have trades in their history
        assertFalse(buyer.getOrderHistory().getAllTrades().isEmpty());
        assertFalse(seller.getOrderHistory().getAllTrades().isEmpty());
    }

    @Test
    @DisplayName("Should handle partial order fills")
    void testPartialFills() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createUser("BUYER", "Buyer User");
        stockSim.createUser("SELLER", "Seller User");

        User seller = (User) stockSim.getTraders().get("SELLER");
        seller.getPortfolio().addStock("AAPL", 50);

        Order sellOrder = new Order(Order.Side.SELL, "AAPL", money("150.00"), 50, "SELLER");
        stockSim.placeOrder(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, "AAPL", money("150.00"), 100, "BUYER");
        stockSim.placeOrder(buyOrder);

        // Sell order should be fully filled
        assertTrue(sellOrder.isFilled());

        // Buy order should be partially filled
        assertFalse(buyOrder.isFilled());
        assertEquals(50, buyOrder.getRemainingQuantity());
    }

    @Test
    @DisplayName("Should notify observers on price update")
    void testPriceUpdateNotification() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createUser("BUYER", "Buyer User");
        stockSim.createUser("SELLER", "Seller User");

        AtomicInteger notificationCount = new AtomicInteger(0);

        stockSim.addObserver(new org.team27.stocksim.observer.IModelObserver() {
            @Override
            public void onPriceUpdate(java.util.HashMap<String, ? extends InstrumentDTO> stocks) {
                notificationCount.incrementAndGet();
            }

            @Override
            public void onStocksChanged(Object payload) {}

            @Override
            public void onPortfolioChanged() {}

            @Override
            public void onTradeSettled() {}
        });

        User seller = (User) stockSim.getTraders().get("SELLER");
        seller.getPortfolio().addStock("AAPL", 100);

        Order sellOrder = new Order(Order.Side.SELL, "AAPL", money("50.00"), 100, "SELLER");
        stockSim.placeOrder(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, "AAPL", money("50.00"), 100, "BUYER");
        stockSim.placeOrder(buyOrder);

        // Should have received notification
        assertTrue(notificationCount.get() > 0);
    }

    @Test
    @DisplayName("Should handle multiple stocks and traders")
    void testMultipleStocksAndTraders() {
        // Create multiple stocks
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createStock("GOOGL", "Google", "0.01", "1", "Technology");
        stockSim.createStock("MSFT", "Microsoft", "0.01", "1", "Technology");

        // Create multiple users
        stockSim.createUser("USER001", "User One");
        stockSim.createUser("USER002", "User Two");
        stockSim.createUser("USER003", "User Three");

        // Create multiple bots
        stockSim.createBot("BOT001", "Bot One");
        stockSim.createBot("BOT002", "Bot Two");

        assertEquals(3, stockSim.getStocks().size());
        assertEquals(5, stockSim.getTraders().size());
    }

    @Test
    @DisplayName("Should prevent insufficient funds trade")
    void testInsufficientFunds() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createUser("BUYER", "Buyer User");
        stockSim.createUser("SELLER", "Seller User");

        User buyer = (User) stockSim.getTraders().get("BUYER");
        User seller = (User) stockSim.getTraders().get("SELLER");

        // Empty buyer's account
        buyer.getPortfolio().withdraw(buyer.getPortfolio().getBalance());

        seller.getPortfolio().addStock("AAPL", 100);

        BigDecimal expensivePrice = money("10000.00");

        Order sellOrder = new Order(Order.Side.SELL, "AAPL", expensivePrice, 100, "SELLER");
        stockSim.placeOrder(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, "AAPL", expensivePrice, 100, "BUYER");
        stockSim.placeOrder(buyOrder);

        // Orders should match but settlement should fail
        // Buyer should not receive stocks
        assertEquals(0, buyer.getPortfolio().getStockQuantity("AAPL"));
    }

    @Test
    @DisplayName("Should track completed trades in order history")
    void testTrackCompletedTrades() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1", "Technology");
        stockSim.createUser("BUYER", "Buyer User");
        stockSim.createUser("SELLER", "Seller User");

        User buyer = (User) stockSim.getTraders().get("BUYER");
        User seller = (User) stockSim.getTraders().get("SELLER");
        seller.getPortfolio().addStock("AAPL", 100);

        Order sellOrder = new Order(Order.Side.SELL, "AAPL", money("50.00"), 100, "SELLER");
        stockSim.placeOrder(sellOrder);

        Order buyOrder = new Order(Order.Side.BUY, "AAPL", money("50.00"), 100, "BUYER");
        stockSim.placeOrder(buyOrder);

        // Trades should be tracked in user order histories
        assertTrue(buyer.getOrderHistory().getAllTrades().size() > 0, "Buyer should have trades in history");
        assertTrue(seller.getOrderHistory().getAllTrades().size() > 0, "Seller should have trades in history");
    }

    @Test
    @DisplayName("Should filter stocks by category")
    void testFilterStocksByCategory() {
        stockSim.createStock("AAPL", "Apple", "0.01", "1", "Technology");
        stockSim.createStock("GOOGL", "Google", "0.01", "1", "Technology");
        stockSim.createStock("JPM", "JP Morgan", "0.01", "1", "Finance");

        var techStocks = stockSim.getStocks("Technology");
        var financeStocks = stockSim.getStocks("Finance");

        assertEquals(2, techStocks.size());
        assertEquals(1, financeStocks.size());
    }
}

