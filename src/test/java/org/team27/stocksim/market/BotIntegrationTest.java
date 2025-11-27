package org.team27.stocksim.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.team27.stocksim.model.market.StockSim;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.users.Trader;
import org.team27.stocksim.model.users.bot.RandomStrategy;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BotIntegrationTest {

    private StockSim stockSim;

    @BeforeEach
    void setUp() {
        stockSim = new StockSim();
    }

    @Test
    void testCreateBotInStockSim() {
        stockSim.createBot("BOT1");

        HashMap<String, Trader> traders = stockSim.getTraders();
        assertTrue(traders.containsKey("BOT1"), "Bot should be added to traders");
    }

    @Test
    void testCreateMultipleBots() {
        stockSim.createBot("BOT1");
        stockSim.createBot("BOT2");
        stockSim.createBot("BOT3");

        HashMap<String, Trader> traders = stockSim.getTraders();
        assertEquals(3, traders.size(), "Should have 3 bots");
        assertTrue(traders.containsKey("BOT1"));
        assertTrue(traders.containsKey("BOT2"));
        assertTrue(traders.containsKey("BOT3"));
    }

    @Test
    void testGetBots() {
        stockSim.createBot("BOT1");
        stockSim.createBot("BOT2");
        stockSim.createUser("USER1");

        HashMap<String, Trader> bots = stockSim.getBots();
        assertEquals(2, bots.size(), "Should only return bots, not users");
        assertTrue(bots.containsKey("BOT1"));
        assertTrue(bots.containsKey("BOT2"));
        assertFalse(bots.containsKey("USER1"), "Users should not be in bots list");
    }

    @Test
    void testBotIsInstanceOfBot() {
        stockSim.createBot("BOT1");

        Trader trader = stockSim.getTraders().get("BOT1");
        assertTrue(trader instanceof Bot, "Created trader should be a Bot instance");
    }

    @Test
    void testDuplicateBotId() {
        stockSim.createBot("BOT1");
        stockSim.createBot("BOT1"); // Try to create duplicate

        HashMap<String, Trader> traders = stockSim.getTraders();
        assertEquals(1, traders.size(), "Duplicate bot ID should not create new bot");
    }

    @Test
    void testBotIdCaseInsensitivity() {
        stockSim.createBot("bot1");

        HashMap<String, Trader> traders = stockSim.getTraders();
        assertTrue(traders.containsKey("BOT1"), "Bot ID should be converted to uppercase");
        assertFalse(traders.containsKey("bot1"), "Lowercase ID should not exist");
    }

    @Test
    void testBotWithStockSimulation() {
        // Create stocks
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "1");
        stockSim.createStock("GOOGL", "Alphabet Inc.", "0.01", "1");

        // Create bot
        stockSim.createBot("BOT1");

        Bot bot = (Bot) stockSim.getTraders().get("BOT1");
        assertNotNull(bot, "Bot should be retrievable from StockSim");

        // Test that bot can interact with strategy
        RandomStrategy strategy = new RandomStrategy();
        assertDoesNotThrow(() -> strategy.decide(stockSim, bot),
                "Bot should be able to use strategy in StockSim context");
    }

    @Test
    void testMultipleBotsWithStrategies() {
        // Create stocks
        stockSim.createStock("AAPL", "Apple Inc.", "100.0", "1");
        stockSim.createStock("MSFT", "Microsoft Corp.", "300.0", "1");

        // Create multiple bots
        stockSim.createBot("BOT1");
        stockSim.createBot("BOT2");
        stockSim.createBot("BOT3");

        // Get bots
        Bot bot1 = (Bot) stockSim.getTraders().get("BOT1");
        Bot bot2 = (Bot) stockSim.getTraders().get("BOT2");
        Bot bot3 = (Bot) stockSim.getTraders().get("BOT3");

        // Create strategies
        RandomStrategy strategy1 = new RandomStrategy();
        RandomStrategy strategy2 = new RandomStrategy();
        RandomStrategy strategy3 = new RandomStrategy();

        // Test that all bots can use strategies
        assertDoesNotThrow(() -> {
            strategy1.decide(stockSim, bot1);
            strategy2.decide(stockSim, bot2);
            strategy3.decide(stockSim, bot3);
        }, "Multiple bots should work with strategies simultaneously");
    }

    @Test
    void testBotAndUserCoexistence() {
        stockSim.createBot("BOT1");
        stockSim.createBot("BOT2");
        stockSim.createUser("USER1");
        stockSim.createUser("USER2");

        HashMap<String, Trader> allTraders = stockSim.getTraders();
        HashMap<String, Trader> bots = stockSim.getBots();

        assertEquals(4, allTraders.size(), "Should have 4 total traders");
        assertEquals(2, bots.size(), "Should have 2 bots");
    }

    @Test
    void testBotStrategyWithEmptyMarket() {
        stockSim.createBot("BOT1");
        Bot bot = (Bot) stockSim.getTraders().get("BOT1");

        RandomStrategy strategy = new RandomStrategy();

        // Should handle empty market gracefully
        assertDoesNotThrow(() -> strategy.decide(stockSim, bot),
                "Bot strategy should handle empty market without crashing");
    }

    @Test
    void testSimulationWithBotsOverTime() {
        // Setup market
        stockSim.createStock("AAPL", "Apple Inc.", "150.0", "1");
        stockSim.createStock("GOOGL", "Alphabet Inc.", "2800.0", "1");

        // Create bots
        stockSim.createBot("BOT1");
        stockSim.createBot("BOT2");

        Bot bot1 = (Bot) stockSim.getTraders().get("BOT1");
        Bot bot2 = (Bot) stockSim.getTraders().get("BOT2");

        RandomStrategy strategy1 = new RandomStrategy();
        RandomStrategy strategy2 = new RandomStrategy();

        // Simulate multiple time steps
        assertDoesNotThrow(() -> {
            for (int tick = 0; tick < 20; tick++) {
                strategy1.decide(stockSim, bot1);
                strategy2.decide(stockSim, bot2);
            }
        }, "Bots should work over multiple simulation ticks");
    }

    @Test
    void testManyBotsScalability() {
        // Create many stocks
        for (int i = 0; i < 10; i++) {
            stockSim.createStock("STK" + i, "Stock " + i, "0.01", "1");
        }

        // Create many bots
        for (int i = 0; i < 20; i++) {
            stockSim.createBot("BOT" + i);
        }

        HashMap<String, Trader> bots = stockSim.getBots();
        assertEquals(20, bots.size(), "Should have created 20 bots");

        // Test that all bots can use strategies
        for (String botId : bots.keySet()) {
            Bot bot = (Bot) bots.get(botId);
            RandomStrategy strategy = new RandomStrategy();

            assertDoesNotThrow(() -> strategy.decide(stockSim, bot),
                    "Each bot should work with strategy: " + botId);
        }
    }

    @Test
    void testBotRetrievalAfterCreation() {
        stockSim.createBot("TESTBOT");

        HashMap<String, Trader> traders = stockSim.getTraders();
        Trader retrieved = traders.get("TESTBOT");

        assertNotNull(retrieved, "Bot should be retrievable after creation");
        assertTrue(retrieved instanceof Bot, "Retrieved trader should be a Bot");
    }
}
