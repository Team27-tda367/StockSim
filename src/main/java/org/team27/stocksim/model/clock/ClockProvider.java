package org.team27.stocksim.model.clock;

/**
 * Provides global access to the application's clock for testable time management.
 *
 * <p>ClockProvider implements the Service Locator pattern to provide a single
 * source of time throughout the application. This abstraction enables the use
 * of simulated time (GameClock) during runtime and makes time-dependent code
 * easily testable by allowing clock substitution.</p>
 *
 * <p><strong>Design Pattern:</strong> Service Locator + Singleton</p>
 * <ul>
 *   <li>Global access point for application clock</li>
 *   <li>Enables dependency injection of time source</li>
 *   <li>Supports both real time and simulated time</li>
 *   <li>Makes time-dependent code testable</li>
 *   <li>Thread-safe static access</li>
 * </ul>
 *
 * <h2>Usage Pattern:</h2>
 * <pre>{@code
 * // At application startup, set the game clock
 * GameClock gameClock = new GameClock(
 *     ZoneId.systemDefault(),
 *     Instant.now(),
 *     60.0  // 60x speed
 * );
 * ClockProvider.setClock(gameClock);
 *
 * // Throughout application, use ClockProvider instead of System.currentTimeMillis()
 * long currentTime = ClockProvider.currentTimeMillis();
 * Instant now = ClockProvider.getClock().instant();
 *
 * // In tests, can reset to system clock
 * ClockProvider.reset();
 *
 * // Or inject a fixed clock for deterministic tests
 * Clock fixedClock = Clock.fixed(testInstant, ZoneId.systemDefault());
 * ClockProvider.setClock(fixedClock);
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see GameClock
 * @see Clock
 */
public class ClockProvider {

    private static GameClock clock = new GameClock();

    private ClockProvider() {
        // Prevent instantiation
    }

    /**
     * Sets the clock to be used throughout the application.
     *
     * <p>This should be called when the simulation starts to enable
     * time acceleration via GameClock.</p>
     *
     * @param gameClock The clock to use (typically a GameClock instance)
     */
    public static void setClock(GameClock gameClock) {
        clock = gameClock;
    }

    /**
     * Gets the current time in milliseconds since epoch.
     *
     * <p>Uses the configured clock (game clock if set, otherwise system clock).
     * This method should be used instead of System.currentTimeMillis() for
     * simulation compatibility.</p>
     *
     * @return Current time in milliseconds
     */
    public static long currentTimeMillis() {
        return clock.millis();
    }

    /**
     * Gets the current clock instance.
     *
     * @return The configured Clock
     */
    public static GameClock getClock() {
        return clock;
    }

    /**
     * Resets to system clock.
     *
     * <p>Useful for testing and cleanup. Restores default behavior
     * where time advances at real-world rate.</p>
     */
    public static void reset() {
        clock = new GameClock();
    }

}
