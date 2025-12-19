package org.team27.stocksim.model.users;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manages asynchronous execution of bot trading actions.
 *
 * <p>BotActionExecutor provides a thread pool for executing bot trading actions
 * concurrently, allowing multiple bots to make trading decisions and place
 * orders in parallel. This improves simulation performance and realism by
 * preventing bots from blocking each other.</p>
 *
 * <p><strong>Design Pattern:</strong> Executor + Thread Pool</p>
 * <ul>
 *   <li>Fixed thread pool sized to available CPU cores</li>
 *   <li>Asynchronous bot action execution</li>
 *   <li>Graceful shutdown with timeout handling</li>
 *   <li>Prevents bot actions from blocking simulation ticks</li>
 *   <li>Enables concurrent bot trading</li>
 * </ul>
 *
 * <h2>Lifecycle:</h2>
 * <ol>
 *   <li>Constructed with thread pool sized to processor count</li>
 *   <li>Bot actions submitted via submit() method</li>
 *   <li>Actions execute asynchronously in thread pool</li>
 *   <li>Shutdown called when simulation ends</li>
 *   <li>Waits for pending actions to complete</li>
 * </ol>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * BotActionExecutor executor = new BotActionExecutor();
 *
 * // On each simulation tick, bots submit actions
 * for (Bot bot : bots) {
 *     List<Order> orders = bot.getStrategy().decide(model, bot);
 *     BotAction action = new BotAction(bot, orders, model);
 *     executor.submit(action);
 * }
 *
 * // When simulation ends, gracefully shutdown
 * executor.shutdown();  // Waits for all pending actions to complete
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see BotAction
 * @see Bot
 * @see ExecutorService
 */
public class BotActionExecutor {
    /**
     * Thread pool for executing bot actions.
     * Sized to available processor count for optimal parallelism.
     */
    private final ExecutorService executorService;

    /**
     * Constructs a BotActionExecutor with a fixed thread pool.
     *
     * <p>The thread pool size is set to the number of available processors,
     * providing good parallelism without excessive context switching.</p>
     */
    public BotActionExecutor() {
        int threads = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(threads);
    }

    /**
     * Submits a bot action for asynchronous execution.
     *
     * <p>The action will be executed in the thread pool when a thread
     * becomes available. This method returns immediately without waiting
     * for execution to complete.</p>
     *
     * @param action The bot action to execute
     */
    public void submit(BotAction action) {
        executorService.submit(action::execute);
    }

    /**
     * Gracefully shuts down the executor.
     *
     * <p>Waits for all currently executing and queued actions to complete
     * before shutting down. If actions don't complete within a reasonable
     * time, forces shutdown. Handles interruption gracefully.</p>
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
    }
}

