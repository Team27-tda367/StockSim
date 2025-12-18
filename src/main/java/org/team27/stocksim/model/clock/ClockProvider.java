package org.team27.stocksim.model.clock;

import java.time.Clock;

/**
 * Provides access to the game's clock throughout the application.
 * This allows all components to use simulated time instead of system time.
 */
public class ClockProvider {

    private static Clock clock = Clock.systemDefaultZone();

    /**
     * Set the game clock to be used throughout the application.
     * Should be called when the simulation starts.
     */
    public static void setClock(Clock gameClock) {
        clock = gameClock;
    }

    /**
     * Get the current game time in milliseconds.
     * Uses game clock if set, otherwise falls back to system time.
     */
    public static long currentTimeMillis() {
        return clock.millis();
    }

    /**
     * Get the current clock instance.
     */
    public static Clock getClock() {
        return clock;
    }

    /**
     * Reset to system clock (useful for testing).
     */
    public static void reset() {
        clock = Clock.systemDefaultZone();
    }
}
