package org.team27.stocksim.model.clock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Accelerated simulation clock that runs faster than real time.
 *
 * <p>GameClock extends Java's Clock class to provide time-accelerated simulation
 * capabilities. It allows the simulation to run at configurable speeds (e.g., 60x
 * real-time), making it possible to simulate hours or days of trading in minutes.
 * The clock maintains a consistent relationship between real-world time and
 * simulated time.</p>
 *
 * <p><strong>Design Pattern:</strong> Adapter (adapts real time to simulated time)</p>
 * <ul>
 *   <li>Configurable time acceleration (speed multiplier)</li>
 *   <li>Maintains real-time to sim-time mapping</li>
 *   <li>Extends java.time.Clock for standard time API compatibility</li>
 *   <li>Dynamic speed adjustment without time discontinuities</li>
 *   <li>Enables testable time-dependent code</li>
 * </ul>
 *
 * <h2>Time Calculation:</h2>
 * <pre>
 * simulated_time = initial_sim_time + (real_elapsed_nanos * speed)
 *
 * Example with 60x speed:
 *   1 real second = 60 simulated seconds
 *   1 real minute = 1 simulated hour
 * </pre>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Create clock starting at market open, running at 60x speed
 * Instant marketOpen = Instant.parse("2024-01-15T09:30:00Z");
 * GameClock clock = new GameClock(
 *     ZoneId.of("America/New_York"),
 *     marketOpen,
 *     60.0  // 60x real-time
 * );
 *
 * // Use as ClockProvider for time-dependent code
 * ClockProvider.setClock(clock);
 *
 * // Check current simulated time
 * Instant simTime = clock.instant();
 *
 * // Adjust speed mid-simulation
 * clock.setSpeed(120.0);  // Speed up to 120x
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see ClockProvider
 * @see Clock
 */
public class GameClock extends Clock {

    /**
     * Time zone for this clock.
     */
    private final ZoneId zone;

    /**
     * Simulated time corresponding to when the clock was started/reset.
     */
    private Instant simStart;

    /**
     * Real-world nanoTime() when simStart was set.
     */
    private long realStartNanos;

    /**
     * Speed multiplier (1.0 = real-time, 60.0 = 60x, etc.).
     */
    private double speed;

    /**
     * Constructs a GameClock with specified initial time and speed.
     *
     * @param zone Time zone for the clock
     * @param initialSimTime Starting simulated time
     * @param speed Time acceleration multiplier (1.0 = real-time)
     */
    public GameClock(ZoneId zone, Instant initialSimTime, double speed) {
        this.zone = zone;
        this.simStart = initialSimTime;
        this.realStartNanos = System.nanoTime();
        this.speed = speed;
    }

    public GameClock() {
        this(ZoneId.systemDefault(), Instant.EPOCH, 1.0);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new GameClock(zone, instant(), speed);
    }

    @Override
    public Instant instant() {
        long nanosSinceStart = System.nanoTime() - realStartNanos;
        long scaledNanos = (long) (nanosSinceStart * speed);
        return simStart.plusNanos(scaledNanos);
    }

    public void setSpeed(double newSpeed) {
        // Frys nuvarande simtid och starta om referensen
        Instant nowSim = instant();
        this.simStart = nowSim;
        this.realStartNanos = System.nanoTime();
        this.speed = newSpeed;
    }

    public double getSpeed() {
        return speed;
    }
}
