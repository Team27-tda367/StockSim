package org.team27.stocksim.model.clock;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Generates periodic tick events based on simulated time progression.
 *
 * <p>GameTicker monitors a GameClock and fires callbacks for each simulated second
 * that passes, even when multiple simulated seconds occur between real-world ticks.
 * This ensures no simulated time is skipped when running at high speed multipliers.</p>
 *
 * <p><strong>Design Pattern:</strong> Observer + Scheduler</p>
 * <ul>
 *   <li>Polls GameClock at configurable real-time intervals</li>
 *   <li>Detects simulated seconds that have passed</li>
 *   <li>Fires callbacks for each simulated second (no skipping)</li>
 *   <li>Handles high-speed simulations correctly (e.g., 100x speed)</li>
 *   <li>Single-threaded executor for predictable callback order</li>
 * </ul>
 *
 * <h2>Tick Logic:</h2>
 * <pre>
 * Real tick (every 200ms default):
 *   1. Check current simulated time
 *   2. Calculate seconds elapsed since last check
 *   3. Fire callback for EACH elapsed second
 *   4. Update last known second
 *
 * Example at 60x speed:
 *   - Real tick interval: 200ms
 *   - Simulated time per tick: 12 seconds
 *   - Callbacks fired: 12 (one per simulated second)
 * </pre>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * GameClock clock = new GameClock(ZoneId.systemDefault(), Instant.now(), 60.0);
 *
 * GameTicker ticker = new GameTicker(
 *     clock,
 *     simInstant -> {
 *         System.out.println("Simulated second: " + simInstant);
 *         // Execute per-second simulation logic
 *     },
 *     200  // Check every 200ms real-time
 * );
 *
 * ticker.start();
 * // ... simulation runs
 * ticker.stop();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see GameClock
 * @see MarketSimulator
 */
public class GameTicker {

    /**
     * The game clock being monitored for time progression.
     */
    private final GameClock clock;

    /**
     * Single-threaded executor for periodic tick checks.
     */
    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

    /**
     * Real-time interval between tick checks in milliseconds.
     */
    private final long tickIntervalMillis;

    /**
     * Last simulated second that was processed.
     */
    private long lastSecond;

    /**
     * Callback invoked for each simulated second that passes.
     */
    private Consumer<Instant> onSecondHandler;

    /**
     * Constructs a GameTicker with default 200ms tick interval.
     *
     * @param clock The game clock to monitor
     * @param onSecondHandler Callback invoked for each simulated second
     */
    public GameTicker(GameClock clock, Consumer<Instant> onSecondHandler) {
        this(clock, onSecondHandler, 200);
    }

    /**
     * Constructs a GameTicker with custom tick interval.
     *
     * @param clock The game clock to monitor
     * @param onSecondHandler Callback invoked for each simulated second
     * @param tickIntervalMillis Real-time milliseconds between tick checks
     */
    public GameTicker(GameClock clock, Consumer<Instant> onSecondHandler, long tickIntervalMillis) {
        this.clock = clock;
        this.onSecondHandler = onSecondHandler;
        this.tickIntervalMillis = tickIntervalMillis;
    }

    /**
     * Starts the ticker, beginning periodic tick checks.
     *
     * <p>Initializes the last known second and schedules periodic tick checks
     * at the configured interval.</p>
     */
    public void start() {
        lastSecond = clock.instant().getEpochSecond();
        exec.scheduleAtFixedRate(this::tick, 0, tickIntervalMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Checks for elapsed simulated seconds and fires callbacks.
     *
     * <p>Called periodically by the executor. Compares current simulated time
     * with last known time and fires callbacks for each elapsed second.</p>
     */
    private void tick() {
        Instant nowSim = clock.instant();
        long currentSec = nowSim.getEpochSecond();

        while (lastSecond < currentSec) {
            lastSecond++;
            onSimulatedSecond(Instant.ofEpochSecond(lastSecond));
        }
    }

    /**
     * Invokes the callback for a single simulated second.
     *
     * @param simSecond The simulated time instant for this second
     */
    private void onSimulatedSecond(Instant simSecond) {
        // Call-backen – här händer magin:
        this.onSecondHandler.accept(simSecond);
    }

    /**
     * Stops the ticker and shuts down the executor.
     *
     * <p>Attempts graceful shutdown, then forces if necessary.
     * Blocks until shutdown completes or timeout occurs.</p>
     */
    public void stop() {
        exec.shutdown();
        try {
            if (!exec.awaitTermination(1, TimeUnit.SECONDS)) {
                exec.shutdownNow();
            }
        } catch (InterruptedException e) {
            exec.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Updates the callback handler dynamically.
     *
     * @param onSecondHandler The new callback to invoke for each simulated second
     */
    public void setCallback(Consumer<Instant> onSecondHandler) {
        this.onSecondHandler = onSecondHandler;

    }
}
