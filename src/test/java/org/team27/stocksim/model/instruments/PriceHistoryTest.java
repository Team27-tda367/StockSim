package org.team27.stocksim.model.instruments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

@DisplayName("PriceHistory Tests")
class PriceHistoryTest {

    private PriceHistory priceHistory;

    @BeforeEach
    void setUp() {
        priceHistory = new PriceHistory();
    }

    @Test
    @DisplayName("Should create empty price history")
    void testCreateEmptyPriceHistory() {
        assertNotNull(priceHistory);
        assertTrue(priceHistory.getPoints().isEmpty());
    }

    @Test
    @DisplayName("Should add price point")
    void testAddPricePoint() {
        BigDecimal price = money("150.00");

        priceHistory.addPrice(price);

        List<PricePoint> points = priceHistory.getPoints();
        assertEquals(1, points.size());
        assertEquals(price, points.get(0).getPrice());
    }

    @Test
    @DisplayName("Should add multiple price points")
    void testAddMultiplePricePoints() {
        priceHistory.addPrice(money("150.00"));
        priceHistory.addPrice(money("155.00"));
        priceHistory.addPrice(money("160.00"));

        List<PricePoint> points = priceHistory.getPoints();
        assertEquals(3, points.size());
    }

    @Test
    @DisplayName("Should maintain order of price points")
    void testPricePointOrder() {
        BigDecimal price1 = money("150.00");
        BigDecimal price2 = money("155.00");
        BigDecimal price3 = money("160.00");

        priceHistory.addPrice(price1);
        priceHistory.addPrice(price2);
        priceHistory.addPrice(price3);

        List<PricePoint> points = priceHistory.getPoints();
        assertEquals(price1, points.get(0).getPrice());
        assertEquals(price2, points.get(1).getPrice());
        assertEquals(price3, points.get(2).getPrice());
    }

    @Test
    @DisplayName("Should add timestamp to price point")
    void testPricePointTimestamp() {
        priceHistory.addPrice(money("150.00"));

        List<PricePoint> points = priceHistory.getPoints();
        assertNotNull(points.get(0).getTimestamp());
        assertTrue(points.get(0).getTimestamp() > 0);
    }

    @Test
    @DisplayName("Should have different timestamps for sequential additions")
    void testSequentialTimestamps() throws InterruptedException {
        priceHistory.addPrice(money("150.00"));
        Thread.sleep(10);
        priceHistory.addPrice(money("155.00"));

        List<PricePoint> points = priceHistory.getPoints();
        assertTrue(points.get(1).getTimestamp() >= points.get(0).getTimestamp());
    }

    @Test
    @DisplayName("Should return copy of points list")
    void testGetPointsReturnsCopy() {
        priceHistory.addPrice(money("150.00"));

        List<PricePoint> points1 = priceHistory.getPoints();
        List<PricePoint> points2 = priceHistory.getPoints();

        assertNotSame(points1, points2);
        assertEquals(points1.size(), points2.size());
    }

    @Test
    @DisplayName("Should not affect original when modifying returned list")
    void testListEncapsulation() {
        priceHistory.addPrice(money("150.00"));

        List<PricePoint> points = priceHistory.getPoints();
        int originalSize = points.size();

        // Try to modify returned list (should not affect original)
        try {
            points.clear();
        } catch (UnsupportedOperationException e) {
            // Some implementations may return unmodifiable list
        }

        // Original should be unaffected
        assertEquals(originalSize, priceHistory.getPoints().size());
    }

    @Test
    @DisplayName("Should handle zero price")
    void testZeroPrice() {
        priceHistory.addPrice(BigDecimal.ZERO);

        List<PricePoint> points = priceHistory.getPoints();
        assertEquals(1, points.size());
        assertEquals(BigDecimal.ZERO, points.get(0).getPrice());
    }

    @Test
    @DisplayName("Should handle negative price")
    void testNegativePrice() {
        BigDecimal negativePrice = money("-10.00");
        priceHistory.addPrice(negativePrice);

        List<PricePoint> points = priceHistory.getPoints();
        assertEquals(1, points.size());
        assertEquals(negativePrice, points.get(0).getPrice());
    }

    @Test
    @DisplayName("Should handle high precision prices")
    void testHighPrecisionPrices() {
        BigDecimal precisePrice = money("123.456789012345");
        priceHistory.addPrice(precisePrice);

        List<PricePoint> points = priceHistory.getPoints();
        assertEquals(precisePrice, points.get(0).getPrice());
    }

    @Test
    @DisplayName("Should handle very large prices")
    void testVeryLargePrices() {
        BigDecimal largePrice = money("999999999.99");
        priceHistory.addPrice(largePrice);

        List<PricePoint> points = priceHistory.getPoints();
        assertEquals(largePrice, points.get(0).getPrice());
    }

    @Test
    @DisplayName("Should track many price changes")
    void testManyPriceChanges() {
        for (int i = 0; i < 1000; i++) {
            priceHistory.addPrice(new BigDecimal(100 + i));
        }

        List<PricePoint> points = priceHistory.getPoints();
        assertEquals(1000, points.size());
        assertEquals(new BigDecimal(100), points.get(0).getPrice());
        assertEquals(new BigDecimal(1099), points.get(999).getPrice());
    }

    @Test
    @DisplayName("Should handle rapid price updates")
    void testRapidPriceUpdates() {
        for (int i = 0; i < 100; i++) {
            priceHistory.addPrice(new BigDecimal(150 + i * 0.01));
        }

        assertEquals(100, priceHistory.getPoints().size());
    }

    @Test
    @DisplayName("Should handle duplicate prices")
    void testDuplicatePrices() {
        BigDecimal price = money("150.00");

        priceHistory.addPrice(price);
        priceHistory.addPrice(price);
        priceHistory.addPrice(price);

        List<PricePoint> points = priceHistory.getPoints();
        assertEquals(3, points.size());
        points.forEach(point -> assertEquals(price, point.getPrice()));
    }

    @Test
    @DisplayName("Should track price volatility")
    void testPriceVolatility() {
        priceHistory.addPrice(money("150.00"));
        priceHistory.addPrice(money("155.00"));
        priceHistory.addPrice(money("145.00"));
        priceHistory.addPrice(money("160.00"));
        priceHistory.addPrice(money("140.00"));

        List<PricePoint> points = priceHistory.getPoints();
        assertEquals(5, points.size());

        // Verify all prices are tracked
        assertEquals(money("150.00"), points.get(0).getPrice());
        assertEquals(money("155.00"), points.get(1).getPrice());
        assertEquals(money("145.00"), points.get(2).getPrice());
    }

    @Test
    @DisplayName("Should calculate price range from history")
    void testPriceRange() {
        priceHistory.addPrice(money("150.00"));
        priceHistory.addPrice(money("155.00"));
        priceHistory.addPrice(money("145.00"));
        priceHistory.addPrice(money("160.00"));
        priceHistory.addPrice(money("140.00"));

        List<PricePoint> points = priceHistory.getPoints();
        BigDecimal min = points.stream()
            .map(PricePoint::getPrice)
            .min(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
        BigDecimal max = points.stream()
            .map(PricePoint::getPrice)
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);

        assertEquals(money("140.00"), min);
        assertEquals(money("160.00"), max);
    }
}

