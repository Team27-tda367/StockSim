package org.team27.stocksim.model.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.team27.stocksim.model.StockSim;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for multi-threaded bot execution with BotActionExecutor.
 */
public class BotMultiThreadedTest {

    private StockSim stockSim;

    @BeforeEach
    void setUp() {
        stockSim = new StockSim();
        // Create some stocks for bots to trade
        stockSim.createStock("AAPL", "Apple Inc.", "0.01", "100", "Technology", "100");
        stockSim.createStock("GOOGL", "Alphabet Inc.", "0.01", "100", "Technology", "100");
        stockSim.createStock("MSFT", "Microsoft Corp.", "0.01", "100", "Technology", "100");
    }

    @Test
    void testBotStateTransition() {
        // Create a bot
        stockSim.createBot("BOT001", "Test Bot");
        Bot bot = stockSim.getBots().get("BOT001");

        // Initial state should be IDLE
        assertEquals(BotState.IDLE, bot.getState());
    }

    @Test
    void testBotTickWithNoOrders() {
        stockSim.createBot("BOT001", "Test Bot");
        Bot bot = stockSim.getBots().get("BOT001");
        BotActionExecutor executor = new BotActionExecutor();

        // Bot with no orders should remain IDLE
        bot.tick(stockSim, executor);

        // Give a moment for any async processing
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertEquals(BotState.IDLE, bot.getState());
        executor.shutdown();
    }

    @Test
    void testMultipleBotsConcurrentExecution() throws InterruptedException {
        // Create multiple bots
        for (int i = 1; i <= 10; i++) {
            stockSim.createBot("BOT" + String.format("%03d", i), "Bot " + i);
        }

        BotActionExecutor executor = new BotActionExecutor();

        // Trigger tick for all bots
        for (Bot bot : stockSim.getBots().values()) {
            bot.tick(stockSim, executor);
        }

        // Wait for all actions to complete
        executor.shutdown();

        // All bots should be back to IDLE state
        for (Bot bot : stockSim.getBots().values()) {
            assertEquals(BotState.IDLE, bot.getState(),
                "Bot " + bot.getId() + " should be IDLE");
        }
    }

    @Test
    void testBotCannotActTwiceConcurrently() throws InterruptedException {
        stockSim.createBot("BOT001", "Test Bot");
        Bot bot = stockSim.getBots().get("BOT001");
        BotActionExecutor executor = new BotActionExecutor();

        CountDownLatch latch = new CountDownLatch(10);

        // Try to trigger multiple ticks concurrently
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                bot.tick(stockSim, executor);
                latch.countDown();
            }).start();
        }

        // Wait for all threads to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS));

        // Wait for executor to finish
        executor.shutdown();

        // Bot should be back to IDLE
        assertEquals(BotState.IDLE, bot.getState());
    }

    @Test
    void testSimulationWithBots() throws InterruptedException {
        // Create several bots
        for (int i = 1; i <= 5; i++) {
            stockSim.createBot("BOT" + String.format("%03d", i), "Bot " + i);
        }

        // Start simulation
        stockSim.startMarketSimulation();

        // Let it run for a short time
        Thread.sleep(2000);

        // Stop simulation
        stockSim.stopMarketSimulation();

        // All bots should be in IDLE state after stopping
        for (Bot bot : stockSim.getBots().values()) {
            assertEquals(BotState.IDLE, bot.getState());
        }
    }
}

