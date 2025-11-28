package org.team27.stocksim.users.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.team27.stocksim.model.market.StockSim;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.users.bot.BotStrategy;
import org.team27.stocksim.model.users.bot.RandomStrategy;
import org.team27.stocksim.model.portfolio.Portfolio;

import static org.junit.jupiter.api.Assertions.*;

class BotStrategyTest {

    private StockSim stockSim;
    private Bot bot;

    @BeforeEach
    void setUp() {
        stockSim = new StockSim();
        bot = new Bot("TESTBOT", "Test Bot", new Portfolio(10000));
    }

    @Test
    void testBotStrategyInterface() {
        BotStrategy strategy = new RandomStrategy();
        assertNotNull(strategy, "BotStrategy implementation should exist");
    }

    @Test
    void testStrategyPolymorphism() {
        // Test that we can use BotStrategy as an interface
        BotStrategy strategy = new RandomStrategy();

        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1");

        assertDoesNotThrow(() -> strategy.decide(stockSim, bot),
                "BotStrategy interface should allow polymorphic usage");
    }

    @Test
    void testMultipleStrategyImplementations() {
        BotStrategy strategy1 = new RandomStrategy();
        BotStrategy strategy2 = new RandomStrategy();

        assertNotNull(strategy1);
        assertNotNull(strategy2);
        assertNotSame(strategy1, strategy2, "Different strategy instances should be different objects");
    }

    @Test
    void testStrategyWithDifferentBots() {
        BotStrategy strategy = new RandomStrategy();

        Bot bot1 = new Bot("BOT1", "Bot 1", new Portfolio(10000));
        Bot bot2 = new Bot("BOT2", "Bot 2", new Portfolio(10000));

        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1");

        assertDoesNotThrow(() -> {
            strategy.decide(stockSim, bot1);
            strategy.decide(stockSim, bot2);
        }, "Strategy should work with different bots");
    }

    @Test
    void testStrategyDecideMethodExists() {
        BotStrategy strategy = new RandomStrategy();

        // Verify that the decide method can be called
        assertDoesNotThrow(() -> strategy.decide(stockSim, bot),
                "BotStrategy should have a decide method");
    }

    @Test
    void testStrategyCanBeAssignedToBot() {
        BotStrategy strategy = new RandomStrategy();
        Bot testBot = new Bot("STRATEGY_BOT", "Strategy Bot", new Portfolio(10000));

        stockSim.createStock("GOOGL", "Alphabet Inc.", "0.01", "1");

        // Test that a bot can use a strategy
        assertDoesNotThrow(() -> strategy.decide(stockSim, testBot),
                "Bot should be able to use a strategy");
    }
}
