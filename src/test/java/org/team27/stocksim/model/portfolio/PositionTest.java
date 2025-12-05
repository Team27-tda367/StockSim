package org.team27.stocksim.model.portfolio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

/**
 * Unit tests for Position class.
 * Tests position tracking with cost basis and P&L calculations.
 */
class PositionTest {

    private Position position;
    private static final String SYMBOL = "AAPL";

    @BeforeEach
    void setUp() {
        position = new Position(SYMBOL);
    }

    @Test
    void testNewPositionIsEmpty() {
        assertTrue(position.isEmpty());
        assertEquals(0, position.getQuantity());
        assertEquals(BigDecimal.ZERO, position.getTotalCost());
    }

    @Test
    void testAddShares() {
        position.addShares(100, money("150.00"), null);

        assertEquals(100, position.getQuantity());
        assertEquals(money("15000.00"), position.getTotalCost());
        assertEquals(money("150.00"), position.getAverageCost());
    }

    @Test
    void testAverageCostCalculation() {
        // Buy 100 shares at $150
        position.addShares(100, money("150.00"), null);
        // Buy 50 more shares at $160
        position.addShares(50, money("160.00"), null);

        assertEquals(150, position.getQuantity());
        // Total cost: (100 * 150) + (50 * 160) = 15000 + 8000 = 23000
        // Average: 23000 / 150 = 153.33
        assertEquals(money("153.33"), position.getAverageCost());
    }

    @Test
    void testRemoveShares() {
        position.addShares(100, money("150.00"), null);
        boolean success = position.removeShares(30, null);

        assertTrue(success);
        assertEquals(70, position.getQuantity());
        assertFalse(position.isEmpty());
    }

    @Test
    void testRemoveAllShares() {
        position.addShares(100, money("150.00"), null);
        boolean success = position.removeShares(100, null);

        assertTrue(success);
        assertEquals(0, position.getQuantity());
        assertTrue(position.isEmpty());
    }

    @Test
    void testRemoveSharesInsufficientQuantity() {
        position.addShares(50, money("150.00"), null);
        boolean success = position.removeShares(75, null);

        assertFalse(success);
        assertEquals(50, position.getQuantity());
    }

    @Test
    void testUnrealizedPnL() {
        // Buy 100 shares at $150
        position.addShares(100, money("150.00"), null);

        // Current price is $160
        BigDecimal unrealizedPnL = position.getUnrealizedPnL(money("160.00"));

        // Profit: (160 - 150) * 100 = 1000
        assertEquals(money("1000.00"), unrealizedPnL);
    }

    @Test
    void testUnrealizedPnLWithLoss() {
        // Buy 100 shares at $150
        position.addShares(100, money("150.00"), null);

        // Current price is $140
        BigDecimal unrealizedPnL = position.getUnrealizedPnL(money("140.00"));

        // Loss: (140 - 150) * 100 = -1000
        assertEquals(new BigDecimal("-1000.00"), unrealizedPnL);
    }

    @Test
    void testAverageCostAfterPartialSale() {
        // Buy 100 shares at $150
        position.addShares(100, money("150.00"), null);

        BigDecimal avgBefore = position.getAverageCost();

        // Sell 50 shares
        position.removeShares(50, null);

        BigDecimal avgAfter = position.getAverageCost();

        // Average cost should remain the same
        assertEquals(avgBefore, avgAfter);
        assertEquals(money("150.00"), avgAfter);
    }

    @Test
    void testSymbol() {
        assertEquals(SYMBOL, position.getSymbol());
    }

    @Test
    void testAverageCostForEmptyPosition() {
        assertEquals(BigDecimal.ZERO, position.getAverageCost());
    }

    @Test
    void testComplexTradingScenario() {
        // Buy 100 shares at $100
        position.addShares(100, money("100.00"), null);
        assertEquals(money("100.00"), position.getAverageCost());

        // Buy 50 more at $120
        position.addShares(50, money("120.00"), null);
        // Average: (100*100 + 50*120) / 150 = 16000/150 = 106.67
        assertEquals(money("106.67"), position.getAverageCost());

        // Sell 75 shares
        position.removeShares(75, null);
        assertEquals(75, position.getQuantity());
        // Average cost should still be 106.67
        assertEquals(money("106.67"), position.getAverageCost());

        // Buy 25 more at $110
        position.addShares(25, money("110.00"), null);
        assertEquals(100, position.getQuantity());
        // New average: (75*106.67 + 25*110) / 100 = (8000.25 + 2750) / 100 = 107.50
        assertEquals(money("107.50"), position.getAverageCost());
    }
}
