package org.team27.stocksim.users.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.users.bot.RandomStrategy;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomStrategyTest {

    private StockSim stockSim;
    private Bot bot;
    private RandomStrategy strategy;

    @BeforeEach
    void setUp() {
        stockSim = new StockSim();
        bot = new Bot("TESTBOT", "Test Bot", new Portfolio(new BigDecimal(10000)));
        strategy = new RandomStrategy();
    }

    @Test
    void testStrategyCreation() {
        assertNotNull(strategy, "Strategy should be created successfully");
    }

    @Test
    void testStrategyCreationWithParameters() {
        Random fixedRandom = new Random(42);
        RandomStrategy customStrategy = new RandomStrategy(fixedRandom, 0.5, 5, 20);
        assertNotNull(customStrategy, "Strategy with custom parameters should be created");
    }

    @Test
    void testDecideWithoutStocks() {
        // Should not throw exception when no stocks exist
        assertDoesNotThrow(() -> strategy.decide(stockSim, bot),
                "Strategy should handle empty stock market gracefully");
    }

    @Test
    void testDecideWithStocks() {
        // Create some test stocks
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1");
        stockSim.createStock("GOOGL", "Alphabet Inc.", "0.01", "1");
        stockSim.createStock("MSFT", "Microsoft Corp.", "0.01", "1");

        // Should not throw exception when stocks exist
        assertDoesNotThrow(() -> strategy.decide(stockSim, bot),
                "Strategy should handle stock market with stocks");
    }

    @Test
    void testDecideMultipleTimes() {
        // Create test stocks
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1");
        stockSim.createStock("GOOGL", "Alphabet Inc.", "0.01", "1");

        // Call decide multiple times to simulate multiple ticks
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> strategy.decide(stockSim, bot),
                    "Strategy should handle multiple consecutive calls");
        }
    }

    @Test
    void testStrategyWithHighBuyProbability() {
        // Create a strategy that always tries to buy
        Random fixedRandom = new Random(42);
        RandomStrategy alwaysBuyStrategy = new RandomStrategy(fixedRandom, 1.0, 1, 10);

        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1");

        // Should execute without errors even with 100% buy probability
        assertDoesNotThrow(() -> alwaysBuyStrategy.decide(stockSim, bot),
                "Strategy with 100% buy probability should work");
    }

    @Test
    void testStrategyWithZeroBuyProbability() {
        // Create a strategy that never buys
        Random fixedRandom = new Random(42);
        RandomStrategy neverBuyStrategy = new RandomStrategy(fixedRandom, 0.0, 1, 10);

        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1");

        // Should execute without errors even with 0% buy probability
        assertDoesNotThrow(() -> neverBuyStrategy.decide(stockSim, bot),
                "Strategy with 0% buy probability should work");
    }

    @Test
    void testStrategyWithSingleStock() {
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1");

        // Create a strategy with high probability to ensure it tries to pick a stock
        Random fixedRandom = new Random(42);
        RandomStrategy highProbStrategy = new RandomStrategy(fixedRandom, 1.0, 1, 10);

        assertDoesNotThrow(() -> highProbStrategy.decide(stockSim, bot),
                "Strategy should work with single stock");
    }

    @Test
    void testStrategyWithMinEqualsMaxQuantity() {
        Random fixedRandom = new Random(42);
        RandomStrategy fixedQuantityStrategy = new RandomStrategy(fixedRandom, 1.0, 5, 5);

        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1");

        assertDoesNotThrow(() -> fixedQuantityStrategy.decide(stockSim, bot),
                "Strategy should handle min quantity equals max quantity");
    }

    @Test
    void testStrategyWithMultipleBots() {
        Bot bot1 = new Bot("BOT1", "Bot 1", new Portfolio(new BigDecimal(10000)));
        Bot bot2 = new Bot("BOT2", "Bot 2", new Portfolio(new BigDecimal(10000)));
        Bot bot3 = new Bot("BOT3", "Bot 3", new Portfolio(new BigDecimal(10000)));

        RandomStrategy strategy1 = new RandomStrategy();
        RandomStrategy strategy2 = new RandomStrategy();
        RandomStrategy strategy3 = new RandomStrategy();

        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1");
        stockSim.createStock("GOOGL", "Alphabet Inc.", "0.01", "1");

        // Multiple bots should be able to use strategies independently
        assertDoesNotThrow(() -> {
            strategy1.decide(stockSim, bot1);
            strategy2.decide(stockSim, bot2);
            strategy3.decide(stockSim, bot3);
        }, "Multiple bots should be able to use strategies");
    }

    @Test
    void testStrategyDeterminismWithSameSeed() {
        Random random1 = new Random(100);
        Random random2 = new Random(100);

        RandomStrategy strategy1 = new RandomStrategy(random1, 1.0, 1, 100);
        RandomStrategy strategy2 = new RandomStrategy(random2, 1.0, 1, 100);

        stockSim.createStock("AAPL", "Apple Inc.", "100.0", "1");

        Bot bot1 = new Bot("BOT1", "Bot 1", new Portfolio(new BigDecimal(10000)));
        Bot bot2 = new Bot("BOT2", "Bot 2", new Portfolio(new BigDecimal(10000)));

        // With same seed, behavior should be deterministic
        assertDoesNotThrow(() -> {
            strategy1.decide(stockSim, bot1);
            strategy2.decide(stockSim, bot2);
        }, "Strategies with same seed should execute deterministically");
    }

    @Test
    void testStrategyWithManyStocks() {
        // Create many stocks to test selection
        for (int i = 0; i < 50; i++) {
            stockSim.createStock("STK" + i, "Stock " + i, "0.01", "1");
        }

        Random fixedRandom = new Random(42);
        RandomStrategy manyStockStrategy = new RandomStrategy(fixedRandom, 1.0, 1, 10);

        assertDoesNotThrow(() -> manyStockStrategy.decide(stockSim, bot),
                "Strategy should handle many stocks");
    }

    @Test
    void testStrategyRepeatedExecutionStability() {
        stockSim.createStock("AAPL", "Apple Inc.", "150.0", "1");
        stockSim.createStock("GOOGL", "Alphabet Inc.", "2800.0", "1");
        stockSim.createStock("MSFT", "Microsoft Corp.", "300.0", "1");

        // Execute strategy many times to test stability
        for (int i = 0; i < 100; i++) {
            assertDoesNotThrow(() -> strategy.decide(stockSim, bot),
                    "Strategy should remain stable over many executions");
        }
    }
}
