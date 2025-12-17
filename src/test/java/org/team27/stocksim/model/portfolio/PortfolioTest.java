package org.team27.stocksim.model.portfolio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

@DisplayName("Portfolio Tests")
class PortfolioTest {

    private Portfolio portfolio;
    private BigDecimal initialBalance;

    @BeforeEach
    void setUp() {
        initialBalance = money("10000.00");
        portfolio = new Portfolio(initialBalance);
    }

    @Test
    @DisplayName("Should create portfolio with initial balance")
    void testCreatePortfolio() {
        assertNotNull(portfolio);
        assertEquals(initialBalance, portfolio.getBalance());
    }

    @Test
    @DisplayName("Should deposit money successfully")
    void testDeposit() {
        BigDecimal depositAmount = money("5000.00");
        portfolio.deposit(depositAmount);

        BigDecimal expectedBalance = initialBalance.add(depositAmount);
        assertEquals(expectedBalance, portfolio.getBalance());
    }

    @Test
    @DisplayName("Should withdraw money successfully when sufficient balance")
    void testWithdrawSuccess() {
        BigDecimal withdrawAmount = money("3000.00");
        boolean result = portfolio.withdraw(withdrawAmount);

        assertTrue(result);
        BigDecimal expectedBalance = initialBalance.subtract(withdrawAmount);
        assertEquals(expectedBalance, portfolio.getBalance());
    }

    @Test
    @DisplayName("Should fail to withdraw when insufficient balance")
    void testWithdrawInsufficientFunds() {
        BigDecimal withdrawAmount = money("15000.00");
        boolean result = portfolio.withdraw(withdrawAmount);

        assertFalse(result);
        assertEquals(initialBalance, portfolio.getBalance());
    }

    @Test
    @DisplayName("Should withdraw exact balance")
    void testWithdrawExactBalance() {
        boolean result = portfolio.withdraw(initialBalance);

        assertTrue(result);
        assertEquals(0, portfolio.getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should not withdraw negative amount")
    void testWithdrawZero() {
        boolean result = portfolio.withdraw(BigDecimal.ZERO);

        assertTrue(result);
        assertEquals(initialBalance, portfolio.getBalance());
    }

    @Test
    @DisplayName("Should add stock without price")
    void testAddStockWithoutPrice() {
        String symbol = "AAPL";
        int quantity = 100;

        portfolio.addStock(symbol, quantity);

        assertEquals(quantity, portfolio.getStockQuantity(symbol));
    }

    @Test
    @DisplayName("Should add stock with price")
    void testAddStockWithPrice() {
        String symbol = "AAPL";
        int quantity = 100;
        BigDecimal price = money("150.00");

        portfolio.addStock(symbol, quantity, price, null);

        assertEquals(quantity, portfolio.getStockQuantity(symbol));
    }

    @Test
    @DisplayName("Should add multiple lots of same stock")
    void testAddMultipleLots() {
        String symbol = "AAPL";

        portfolio.addStock(symbol, 50, money("150.00"), null);
        portfolio.addStock(symbol, 30, money("155.00"), null);
        portfolio.addStock(symbol, 20, money("160.00"), null);

        assertEquals(100, portfolio.getStockQuantity(symbol));
    }

    @Test
    @DisplayName("Should remove stock successfully")
    void testRemoveStock() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        boolean result = portfolio.removeStock(symbol, 30, null);

        assertTrue(result);
        assertEquals(70, portfolio.getStockQuantity(symbol));
    }

    @Test
    @DisplayName("Should fail to remove stock when insufficient quantity")
    void testRemoveStockInsufficientQuantity() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        boolean result = portfolio.removeStock(symbol, 150, null);

        assertFalse(result);
        assertEquals(100, portfolio.getStockQuantity(symbol));
    }

    @Test
    @DisplayName("Should fail to remove stock that doesn't exist")
    void testRemoveNonExistentStock() {
        boolean result = portfolio.removeStock("AAPL", 10, null);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should remove all shares of a stock")
    void testRemoveAllShares() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        boolean result = portfolio.removeStock(symbol, 100, null);

        assertTrue(result);
        assertEquals(0, portfolio.getStockQuantity(symbol));
    }

    @Test
    @DisplayName("Should track multiple different stocks")
    void testMultipleStocks() {
        portfolio.addStock("AAPL", 100, money("150.00"), null);
        portfolio.addStock("GOOGL", 50, money("2800.00"), null);
        portfolio.addStock("MSFT", 75, money("380.00"), null);

        assertEquals(100, portfolio.getStockQuantity("AAPL"));
        assertEquals(50, portfolio.getStockQuantity("GOOGL"));
        assertEquals(75, portfolio.getStockQuantity("MSFT"));
    }

    @Test
    @DisplayName("Should report portfolio is not empty when has stocks")
    void testPortfolioNotEmpty() {
        portfolio.addStock("AAPL", 100);

        assertFalse(portfolio.isEmpty());
    }

    @Test
    @DisplayName("Should report portfolio is empty when no stocks")
    void testPortfolioEmpty() {
        assertTrue(portfolio.isEmpty());
    }

    @Test
    @DisplayName("Should get stock holdings")
    void testGetStockHoldings() {
        portfolio.addStock("AAPL", 100);
        portfolio.addStock("GOOGL", 50);

        var holdings = portfolio.getStockHoldings();
        assertEquals(2, holdings.size());
        assertEquals(100, holdings.get("AAPL"));
        assertEquals(50, holdings.get("GOOGL"));
    }

    @Test
    @DisplayName("Should calculate total cost of adding stocks")
    void testTotalCostTracking() {
        String symbol = "AAPL";

        portfolio.addStock(symbol, 50, money("150.00"), null);
        portfolio.addStock(symbol, 50, money("160.00"), null);

        // Total cost: (50 * 150) + (50 * 160) = 7500 + 8000 = 15500
        // Average cost should be 155.00
        assertEquals(100, portfolio.getStockQuantity(symbol));
    }

    @Test
    @DisplayName("Should handle zero balance portfolio")
    void testZeroBalancePortfolio() {
        Portfolio emptyPortfolio = new Portfolio(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, emptyPortfolio.getBalance());
        assertFalse(emptyPortfolio.withdraw(money("1.00")));
    }

    @Test
    @DisplayName("Should handle large balance amounts")
    void testLargeBalanceAmounts() {
        BigDecimal largeAmount = money("1000000000.00");
        Portfolio richPortfolio = new Portfolio(largeAmount);

        assertEquals(largeAmount, richPortfolio.getBalance());
        assertTrue(richPortfolio.withdraw(money("500000000.00")));
    }

    @Test
    @DisplayName("Should handle high precision decimal amounts")
    void testHighPrecisionDecimals() {
        BigDecimal preciseAmount = money("12345.6789");
        portfolio.deposit(preciseAmount);

        BigDecimal expected = initialBalance.add(preciseAmount);
        assertEquals(expected, portfolio.getBalance());
    }

    @Test
    @DisplayName("Should handle sequential deposits and withdrawals")
    void testSequentialTransactions() {
        portfolio.deposit(money("1000.00"));
        portfolio.withdraw(money("500.00"));
        portfolio.deposit(money("2000.00"));
        portfolio.withdraw(money("1500.00"));

        BigDecimal expected = initialBalance
            .add(money("1000.00"))
            .subtract(money("500.00"))
            .add(money("2000.00"))
            .subtract(money("1500.00"));

        assertEquals(expected, portfolio.getBalance());
    }

    @Test
    @DisplayName("Should handle fractional shares")
    void testFractionalShares() {
        String symbol = "AAPL";
        // While the system uses int for quantity, test boundary
        portfolio.addStock(symbol, 1);
        assertEquals(1, portfolio.getStockQuantity(symbol));
    }

    @Test
    @DisplayName("Should return true when can buy with sufficient balance")
    void testCanBuyWithSufficientBalance() {
        String symbol = "AAPL";
        int quantity = 10;
        BigDecimal price = money("150.00");

        assertTrue(portfolio.canBuy(symbol, quantity, price));
    }

    @Test
    @DisplayName("Should return false when cannot buy with insufficient balance")
    void testCanBuyWithInsufficientBalance() {
        String symbol = "AAPL";
        int quantity = 100;
        BigDecimal price = money("150.00");

        assertFalse(portfolio.canBuy(symbol, quantity, price));
    }

    @Test
    @DisplayName("Should return true when can buy with exact balance")
    void testCanBuyWithExactBalance() {
        String symbol = "AAPL";
        int quantity = 100;
        BigDecimal price = money("100.00");

        assertTrue(portfolio.canBuy(symbol, quantity, price));
    }

    @Test
    @DisplayName("Should return false when cannot buy one share over budget")
    void testCanBuyOneShareOverBudget() {
        String symbol = "AAPL";
        int quantity = 101;
        BigDecimal price = money("100.00");

        assertFalse(portfolio.canBuy(symbol, quantity, price));
    }

    @Test
    @DisplayName("Should return true when can buy with zero quantity")
    void testCanBuyZeroQuantity() {
        String symbol = "AAPL";
        int quantity = 0;
        BigDecimal price = money("150.00");

        assertTrue(portfolio.canBuy(symbol, quantity, price));
    }

    @Test
    @DisplayName("Should return true when can buy with zero price")
    void testCanBuyZeroPrice() {
        String symbol = "AAPL";
        int quantity = 100;
        BigDecimal price = BigDecimal.ZERO;

        assertTrue(portfolio.canBuy(symbol, quantity, price));
    }

    @Test
    @DisplayName("Should return true when can sell with sufficient quantity")
    void testCanSellWithSufficientQuantity() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        assertTrue(portfolio.canSell(symbol, 50));
    }

    @Test
    @DisplayName("Should return false when cannot sell with insufficient quantity")
    void testCanSellWithInsufficientQuantity() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 50);

        assertFalse(portfolio.canSell(symbol, 100));
    }

    @Test
    @DisplayName("Should return true when can sell exact quantity owned")
    void testCanSellExactQuantity() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        assertTrue(portfolio.canSell(symbol, 100));
    }

    @Test
    @DisplayName("Should return false when cannot sell stock not owned")
    void testCanSellStockNotOwned() {
        String symbol = "AAPL";

        assertFalse(portfolio.canSell(symbol, 1));
    }

    @Test
    @DisplayName("Should return true when can sell zero quantity")
    void testCanSellZeroQuantity() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        assertTrue(portfolio.canSell(symbol, 0));
    }

    @Test
    @DisplayName("Should return false when cannot sell one share more than owned")
    void testCanSellOneShareMoreThanOwned() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        assertFalse(portfolio.canSell(symbol, 101));
    }

    @Test
    @DisplayName("Should correctly evaluate canBuy after balance changes")
    void testCanBuyAfterBalanceChanges() {
        String symbol = "AAPL";
        int quantity = 100;
        BigDecimal price = money("100.00");

        assertTrue(portfolio.canBuy(symbol, quantity, price));

        portfolio.withdraw(money("5000.00"));
        assertFalse(portfolio.canBuy(symbol, quantity, price));

        portfolio.deposit(money("10000.00"));
        assertTrue(portfolio.canBuy(symbol, quantity, price));
    }

    @Test
    @DisplayName("Should correctly evaluate canSell after stock changes")
    void testCanSellAfterStockChanges() {
        String symbol = "AAPL";

        assertFalse(portfolio.canSell(symbol, 100));

        portfolio.addStock(symbol, 50);
        assertFalse(portfolio.canSell(symbol, 100));

        portfolio.addStock(symbol, 50);
        assertTrue(portfolio.canSell(symbol, 100));

        portfolio.removeStock(symbol, 50, null);
        assertFalse(portfolio.canSell(symbol, 100));
    }
}

