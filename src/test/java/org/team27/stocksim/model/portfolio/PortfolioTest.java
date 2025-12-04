package org.team27.stocksim.model.portfolio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.team27.stocksim.model.portfolio.Portfolio;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

/**
 * Unit tests for Portfolio class.
 * Tests cash and stock position management.
 */
class PortfolioTest {

    private Portfolio portfolio;
    private BigDecimal initialBalance;

    @BeforeEach
    void setUp() {
        initialBalance = money("10000.00");
        portfolio = new Portfolio(initialBalance);
    }

    // Cash Management Tests

    @Test
    void testInitialBalance() {
        assertEquals(initialBalance, portfolio.getBalance());
    }

    @Test
    void testDeposit() {
        BigDecimal depositAmount = money("500.00");
        portfolio.deposit(depositAmount);

        assertEquals(initialBalance.add(depositAmount), portfolio.getBalance());
    }

    @Test
    void testMultipleDeposits() {
        portfolio.deposit(money("100.00"));
        portfolio.deposit(money("200.00"));
        portfolio.deposit(money("300.00"));

        assertEquals(initialBalance.add(money("600.00")), portfolio.getBalance());
    }

    @Test
    void testWithdrawSuccess() {
        BigDecimal withdrawAmount = money("5000.00");
        boolean result = portfolio.withdraw(withdrawAmount);

        assertTrue(result);
        assertEquals(initialBalance.subtract(withdrawAmount), portfolio.getBalance());
    }

    @Test
    void testWithdrawExactBalance() {
        boolean result = portfolio.withdraw(initialBalance);

        assertTrue(result);
        assertEquals(0, portfolio.getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testWithdrawInsufficientFunds() {
        BigDecimal withdrawAmount = money("15000.00");
        boolean result = portfolio.withdraw(withdrawAmount);

        assertFalse(result);
        assertEquals(initialBalance, portfolio.getBalance());
    }

    @Test
    void testWithdrawZeroAmount() {
        boolean result = portfolio.withdraw(BigDecimal.ZERO);

        assertTrue(result);
        assertEquals(initialBalance, portfolio.getBalance());
    }

    @Test
    void testMultipleWithdrawals() {
        portfolio.withdraw(money("1000.00"));
        portfolio.withdraw(money("2000.00"));
        portfolio.withdraw(money("500.00"));

        assertEquals(money("6500.00"), portfolio.getBalance());
    }

    // Stock Position Tests

    @Test
    void testAddStockToEmptyPortfolio() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        assertEquals(100, portfolio.getStockQuantity(symbol));
    }

    @Test
    void testAddStockToExistingPosition() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 50);
        portfolio.addStock(symbol, 30);

        assertEquals(80, portfolio.getStockQuantity(symbol));
    }

    @Test
    void testAddMultipleStocks() {
        portfolio.addStock("AAPL", 100);
        portfolio.addStock("GOOGL", 50);
        portfolio.addStock("MSFT", 75);

        assertEquals(100, portfolio.getStockQuantity("AAPL"));
        assertEquals(50, portfolio.getStockQuantity("GOOGL"));
        assertEquals(75, portfolio.getStockQuantity("MSFT"));
    }

    @Test
    void testGetStockQuantityForNonExistentStock() {
        assertEquals(0, portfolio.getStockQuantity("AAPL"));
    }

    @Test
    void testRemoveStockSuccess() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        boolean result = portfolio.removeStock(symbol, 30);

        assertTrue(result);
        assertEquals(70, portfolio.getStockQuantity(symbol));
    }

    @Test
    void testRemoveExactStockQuantity() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        boolean result = portfolio.removeStock(symbol, 100);

        assertTrue(result);
        assertEquals(0, portfolio.getStockQuantity(symbol));
    }

    @Test
    void testRemoveStockInsufficientQuantity() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 50);

        boolean result = portfolio.removeStock(symbol, 75);

        assertFalse(result);
        assertEquals(50, portfolio.getStockQuantity(symbol));
    }

    @Test
    void testRemoveStockNotOwned() {
        boolean result = portfolio.removeStock("AAPL", 10);

        assertFalse(result);
        assertEquals(0, portfolio.getStockQuantity("AAPL"));
    }

    @Test
    void testRemoveZeroStocks() {
        String symbol = "AAPL";
        portfolio.addStock(symbol, 100);

        boolean result = portfolio.removeStock(symbol, 0);

        assertTrue(result);
        assertEquals(100, portfolio.getStockQuantity(symbol));
    }

    @Test
    void testStockHoldingsMapIsCopy() {
        portfolio.addStock("AAPL", 100);

        var holdings = portfolio.getStockHoldings();
        holdings.put("GOOGL", 999); // Modify the returned map

        // Original portfolio should be unchanged
        assertEquals(0, portfolio.getStockQuantity("GOOGL"));
        assertEquals(1, portfolio.getStockHoldings().size());
    }

    @Test
    void testGetStockHoldings() {
        portfolio.addStock("AAPL", 100);
        portfolio.addStock("GOOGL", 50);
        portfolio.addStock("MSFT", 75);

        var holdings = portfolio.getStockHoldings();

        assertEquals(3, holdings.size());
        assertEquals(100, holdings.get("AAPL"));
        assertEquals(50, holdings.get("GOOGL"));
        assertEquals(75, holdings.get("MSFT"));
    }

    @Test
    void testEmptyStockHoldings() {
        var holdings = portfolio.getStockHoldings();

        assertNotNull(holdings);
        assertTrue(holdings.isEmpty());
    }

    // Integration Tests

    @Test
    void testBuyAndSellStockSequence() {
        // Simulate buying stock
        portfolio.withdraw(money("1000.00")); // Pay for stock
        portfolio.addStock("AAPL", 10);

        // Simulate selling stock
        portfolio.removeStock("AAPL", 10);
        portfolio.deposit(money("1100.00")); // Receive payment

        assertEquals(money("10100.00"), portfolio.getBalance());
        assertEquals(0, portfolio.getStockQuantity("AAPL"));
    }

    @Test
    void testMultipleTransactions() {
        // Initial: 10000 cash, 0 stocks

        // Buy AAPL
        portfolio.withdraw(money("5000.00"));
        portfolio.addStock("AAPL", 50);

        // Buy GOOGL
        portfolio.withdraw(money("3000.00"));
        portfolio.addStock("GOOGL", 30);

        // Sell some AAPL
        portfolio.removeStock("AAPL", 20);
        portfolio.deposit(money("2200.00"));

        assertEquals(money("4200.00"), portfolio.getBalance());
        assertEquals(30, portfolio.getStockQuantity("AAPL"));
        assertEquals(30, portfolio.getStockQuantity("GOOGL"));
    }

    @Test
    void testPortfolioStateAfterFailedWithdrawal() {
        BigDecimal initialBalance = portfolio.getBalance();

        portfolio.withdraw(money("15000.00")); // Should fail

        assertEquals(initialBalance, portfolio.getBalance());
    }

    @Test
    void testPortfolioStateAfterFailedStockRemoval() {
        portfolio.addStock("AAPL", 50);

        portfolio.removeStock("AAPL", 100); // Should fail

        assertEquals(50, portfolio.getStockQuantity("AAPL"));
    }

    @Test
    void testAddNegativeQuantityShouldReducePosition() {
        // Note: This tests current behavior - might want to validate input in future
        portfolio.addStock("AAPL", 100);
        portfolio.addStock("AAPL", -30);

        assertEquals(70, portfolio.getStockQuantity("AAPL"));
    }
}
