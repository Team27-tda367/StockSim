package org.team27.stocksim.model.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.portfolio.Portfolio;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

/**
 * Unit tests for trade settlement functionality in StockSim.
 * Tests the complete flow from order placement to portfolio updates.
 */
class TradeSettlementTest {

    private StockSim stockSim;
    private String stockSymbol;
    private String buyerTraderId;
    private String sellerTraderId;

    @BeforeEach
    void setUp() {
        stockSim = new StockSim();

        // Create a stock
        stockSymbol = "AAPL";
        stockSim.createStock(stockSymbol, "Apple Inc.", "0.01", "1");

        // Create two traders
        buyerTraderId = "BUYER1";
        sellerTraderId = "SELLER1";
        stockSim.createUser(buyerTraderId, "Buyer Trader");
        stockSim.createUser(sellerTraderId, "Seller Trader");

        // Give seller initial stock position
        stockSim.getTraders().get(sellerTraderId).getPortfolio().addStock(stockSymbol, 1000);
    }

    @Test
    void testSimpleTradeSettlement() {
        // Arrange: Create matching orders
        Order sellOrder = new Order(Order.Side.SELL, stockSymbol, 1, money("100.00"), 10, sellerTraderId);
        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, 2, money("100.00"), 10, buyerTraderId);

        Portfolio buyerPortfolio = stockSim.getTraders().get(buyerTraderId).getPortfolio();
        Portfolio sellerPortfolio = stockSim.getTraders().get(sellerTraderId).getPortfolio();

        BigDecimal buyerInitialBalance = buyerPortfolio.getBalance();
        BigDecimal sellerInitialBalance = sellerPortfolio.getBalance();
        int sellerInitialStock = sellerPortfolio.getStockQuantity(stockSymbol);

        // Act: Place orders
        stockSim.placeOrder(sellOrder);
        stockSim.placeOrder(buyOrder);

        // Assert: Check portfolio changes
        BigDecimal expectedTradeValue = money("100.00").multiply(BigDecimal.valueOf(10));

        // Buyer should have less cash
        assertEquals(buyerInitialBalance.subtract(expectedTradeValue), buyerPortfolio.getBalance());

        // Seller should have more cash
        assertEquals(sellerInitialBalance.add(expectedTradeValue), sellerPortfolio.getBalance());

        // Buyer should have stocks
        assertEquals(10, buyerPortfolio.getStockQuantity(stockSymbol));

        // Seller should have fewer stocks
        assertEquals(sellerInitialStock - 10, sellerPortfolio.getStockQuantity(stockSymbol));
    }

    @Test
    void testPartialTradeSettlement() {
        // Arrange: Create partially matching orders
        Order sellOrder = new Order(Order.Side.SELL, stockSymbol, 1, money("100.00"), 50, sellerTraderId);
        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, 2, money("100.00"), 30, buyerTraderId);

        Portfolio buyerPortfolio = stockSim.getTraders().get(buyerTraderId).getPortfolio();
        Portfolio sellerPortfolio = stockSim.getTraders().get(sellerTraderId).getPortfolio();

        BigDecimal buyerInitialBalance = buyerPortfolio.getBalance();
        BigDecimal sellerInitialBalance = sellerPortfolio.getBalance();

        // Act: Place orders (only 30 shares should trade)
        stockSim.placeOrder(sellOrder);
        stockSim.placeOrder(buyOrder);

        // Assert: Check only 30 shares traded
        BigDecimal expectedTradeValue = money("100.00").multiply(BigDecimal.valueOf(30));

        assertEquals(buyerInitialBalance.subtract(expectedTradeValue), buyerPortfolio.getBalance());
        assertEquals(sellerInitialBalance.add(expectedTradeValue), sellerPortfolio.getBalance());
        assertEquals(30, buyerPortfolio.getStockQuantity(stockSymbol));
        assertEquals(1000 - 30, sellerPortfolio.getStockQuantity(stockSymbol));
    }

    @Test
    void testMultipleTradesSettlement() {
        // Arrange: Create multiple sell orders and one large buy order
        String seller2Id = "SELLER2";
        String seller3Id = "SELLER3";
        stockSim.createUser(seller2Id, "Seller 2");
        stockSim.createUser(seller3Id, "Seller 3");

        stockSim.getTraders().get(seller2Id).getPortfolio().addStock(stockSymbol, 500);
        stockSim.getTraders().get(seller3Id).getPortfolio().addStock(stockSymbol, 500);

        Order sellOrder1 = new Order(Order.Side.SELL, stockSymbol, 1, money("98.00"), 20, sellerTraderId);
        Order sellOrder2 = new Order(Order.Side.SELL, stockSymbol, 2, money("99.00"), 30, seller2Id);
        Order sellOrder3 = new Order(Order.Side.SELL, stockSymbol, 3, money("100.00"), 40, seller3Id);
        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, 4, money("100.00"), 70, buyerTraderId);

        Portfolio buyerPortfolio = stockSim.getTraders().get(buyerTraderId).getPortfolio();
        BigDecimal buyerInitialBalance = buyerPortfolio.getBalance();

        // Act: Place all orders
        stockSim.placeOrder(sellOrder1);
        stockSim.placeOrder(sellOrder2);
        stockSim.placeOrder(sellOrder3);
        stockSim.placeOrder(buyOrder);

        // Assert: Buyer should receive 70 shares total
        assertEquals(70, buyerPortfolio.getStockQuantity(stockSymbol));

        // Calculate expected cost: 20@98 + 30@99 + 20@100
        BigDecimal expectedCost = money("98.00").multiply(BigDecimal.valueOf(20))
                .add(money("99.00").multiply(BigDecimal.valueOf(30)))
                .add(money("100.00").multiply(BigDecimal.valueOf(20)));

        assertEquals(buyerInitialBalance.subtract(expectedCost), buyerPortfolio.getBalance());
    }

    @Test
    void testInsufficientFundsDoesNotSettleTrade() {
        // Arrange: Create a buyer with insufficient funds
        String poorBuyerId = "POORBUYER";
        stockSim.createUser(poorBuyerId, "Poor Buyer");

        Portfolio poorBuyerPortfolio = stockSim.getTraders().get(poorBuyerId).getPortfolio();

        // Withdraw almost all funds
        poorBuyerPortfolio.withdraw(money("9900.00"));
        BigDecimal poorBuyerInitialBalance = poorBuyerPortfolio.getBalance(); // Should be 100

        Order sellOrder = new Order(Order.Side.SELL, stockSymbol, 1, money("50.00"), 10, sellerTraderId);
        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, 2, money("50.00"), 10, poorBuyerId);

        Portfolio sellerPortfolio = stockSim.getTraders().get(sellerTraderId).getPortfolio();
        BigDecimal sellerInitialBalance = sellerPortfolio.getBalance();
        int sellerInitialStock = sellerPortfolio.getStockQuantity(stockSymbol);

        // Act: Place orders (trade should fail due to insufficient funds)
        stockSim.placeOrder(sellOrder);
        stockSim.placeOrder(buyOrder);

        // Assert: No settlement should occur
        assertEquals(poorBuyerInitialBalance, poorBuyerPortfolio.getBalance());
        assertEquals(sellerInitialBalance, sellerPortfolio.getBalance());
        assertEquals(0, poorBuyerPortfolio.getStockQuantity(stockSymbol));
        assertEquals(sellerInitialStock, sellerPortfolio.getStockQuantity(stockSymbol));
    }

    @Test
    void testSellerStockPositionReduces() {
        // Arrange
        Portfolio sellerPortfolio = stockSim.getTraders().get(sellerTraderId).getPortfolio();
        int initialStock = sellerPortfolio.getStockQuantity(stockSymbol);

        Order sellOrder = new Order(Order.Side.SELL, stockSymbol, 1, money("100.00"), 100, sellerTraderId);
        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, 2, money("100.00"), 100, buyerTraderId);

        // Act
        stockSim.placeOrder(sellOrder);
        stockSim.placeOrder(buyOrder);

        // Assert
        assertEquals(initialStock - 100, sellerPortfolio.getStockQuantity(stockSymbol));
    }

    @Test
    void testBuyerStockPositionIncreasesFromZero() {
        // Arrange
        Portfolio buyerPortfolio = stockSim.getTraders().get(buyerTraderId).getPortfolio();
        assertEquals(0, buyerPortfolio.getStockQuantity(stockSymbol));

        Order sellOrder = new Order(Order.Side.SELL, stockSymbol, 1, money("100.00"), 50, sellerTraderId);
        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, 2, money("100.00"), 50, buyerTraderId);

        // Act
        stockSim.placeOrder(sellOrder);
        stockSim.placeOrder(buyOrder);

        // Assert
        assertEquals(50, buyerPortfolio.getStockQuantity(stockSymbol));
    }

    @Test
    void testCashBalancesCorrectAfterTrade() {
        // Arrange
        Portfolio buyerPortfolio = stockSim.getTraders().get(buyerTraderId).getPortfolio();
        Portfolio sellerPortfolio = stockSim.getTraders().get(sellerTraderId).getPortfolio();

        BigDecimal buyerInitial = buyerPortfolio.getBalance();
        BigDecimal sellerInitial = sellerPortfolio.getBalance();

        BigDecimal tradePrice = money("125.50");
        int quantity = 25;

        Order sellOrder = new Order(Order.Side.SELL, stockSymbol, 1, tradePrice, quantity, sellerTraderId);
        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, 2, tradePrice, quantity, buyerTraderId);

        // Act
        stockSim.placeOrder(sellOrder);
        stockSim.placeOrder(buyOrder);

        // Assert
        BigDecimal expectedTransfer = tradePrice.multiply(BigDecimal.valueOf(quantity));
        assertEquals(buyerInitial.subtract(expectedTransfer), buyerPortfolio.getBalance());
        assertEquals(sellerInitial.add(expectedTransfer), sellerPortfolio.getBalance());

        // Total money in system should be conserved
        assertEquals(0, buyerInitial.add(sellerInitial).compareTo(
                buyerPortfolio.getBalance().add(sellerPortfolio.getBalance())));
    }

    @Test
    void testNoTradeWhenPricesDontMatch() {
        // Arrange
        Portfolio buyerPortfolio = stockSim.getTraders().get(buyerTraderId).getPortfolio();
        Portfolio sellerPortfolio = stockSim.getTraders().get(sellerTraderId).getPortfolio();

        BigDecimal buyerInitial = buyerPortfolio.getBalance();
        BigDecimal sellerInitial = sellerPortfolio.getBalance();

        Order sellOrder = new Order(Order.Side.SELL, stockSymbol, 1, money("105.00"), 10, sellerTraderId);
        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, 2, money("100.00"), 10, buyerTraderId);

        // Act
        stockSim.placeOrder(sellOrder);
        stockSim.placeOrder(buyOrder);

        // Assert: No settlement should occur
        assertEquals(buyerInitial, buyerPortfolio.getBalance());
        assertEquals(sellerInitial, sellerPortfolio.getBalance());
        assertEquals(0, buyerPortfolio.getStockQuantity(stockSymbol));
        assertEquals(1000, sellerPortfolio.getStockQuantity(stockSymbol));
    }

    @Test
    void testSequentialTradesWithSameTraders() {
        // Arrange: Test multiple trades between same traders
        Portfolio buyerPortfolio = stockSim.getTraders().get(buyerTraderId).getPortfolio();
        Portfolio sellerPortfolio = stockSim.getTraders().get(sellerTraderId).getPortfolio();

        BigDecimal buyerInitial = buyerPortfolio.getBalance();

        // Act: First trade
        Order sellOrder1 = new Order(Order.Side.SELL, stockSymbol, 1, money("100.00"), 10, sellerTraderId);
        Order buyOrder1 = new Order(Order.Side.BUY, stockSymbol, 2, money("100.00"), 10, buyerTraderId);
        stockSim.placeOrder(sellOrder1);
        stockSim.placeOrder(buyOrder1);

        // Second trade
        Order sellOrder2 = new Order(Order.Side.SELL, stockSymbol, 3, money("101.00"), 15, sellerTraderId);
        Order buyOrder2 = new Order(Order.Side.BUY, stockSymbol, 4, money("101.00"), 15, buyerTraderId);
        stockSim.placeOrder(sellOrder2);
        stockSim.placeOrder(buyOrder2);

        // Assert
        assertEquals(25, buyerPortfolio.getStockQuantity(stockSymbol));

        BigDecimal expectedCost = money("100.00").multiply(BigDecimal.valueOf(10))
                .add(money("101.00").multiply(BigDecimal.valueOf(15)));
        assertEquals(buyerInitial.subtract(expectedCost), buyerPortfolio.getBalance());
    }

    @Test
    void testReverseOrderEntry() {
        // Arrange: Place buy order first, then matching sell order
        Portfolio buyerPortfolio = stockSim.getTraders().get(buyerTraderId).getPortfolio();
        Portfolio sellerPortfolio = stockSim.getTraders().get(sellerTraderId).getPortfolio();

        BigDecimal buyerInitial = buyerPortfolio.getBalance();
        BigDecimal sellerInitial = sellerPortfolio.getBalance();

        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, 1, money("100.00"), 20, buyerTraderId);
        Order sellOrder = new Order(Order.Side.SELL, stockSymbol, 2, money("100.00"), 20, sellerTraderId);

        // Act: Buy order first
        stockSim.placeOrder(buyOrder);
        stockSim.placeOrder(sellOrder);

        // Assert
        BigDecimal expectedTradeValue = money("100.00").multiply(BigDecimal.valueOf(20));
        assertEquals(buyerInitial.subtract(expectedTradeValue), buyerPortfolio.getBalance());
        assertEquals(sellerInitial.add(expectedTradeValue), sellerPortfolio.getBalance());
        assertEquals(20, buyerPortfolio.getStockQuantity(stockSymbol));
        assertEquals(980, sellerPortfolio.getStockQuantity(stockSymbol));
    }
}
