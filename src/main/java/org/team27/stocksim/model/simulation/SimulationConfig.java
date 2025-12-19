package org.team27.stocksim.model.simulation;

import java.time.Instant;

/**
 * Configuration for market simulation parameters.
 * Encapsulates simulation settings with sensible defaults.
 */
public class SimulationConfig {
    private final int speedupFactor;
    private final int tickInterval;
    private final int durationInRealSeconds;
    private final Instant initialTimestamp;

    private SimulationConfig(Builder builder) {
        this.speedupFactor = builder.speedupFactor;
        this.tickInterval = builder.tickInterval;
        this.durationInRealSeconds = builder.durationInRealSeconds;
        this.initialTimestamp = builder.initialTimestamp;
    }

    public int getSpeedupFactor() {
        return speedupFactor;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public int getDurationInRealSeconds() {
        return durationInRealSeconds;
    }

    public Instant getInitialTimestamp() {
        return initialTimestamp;
    }

    /**
     * Creates a default configuration with standard values.
     * - Speedup factor: 3600 (1 hour of simulation per second of real time)
     * - Tick interval: 50ms
     * - Duration: 10 seconds
     * - Initial timestamp: EPOCH (0)
     */
    public static SimulationConfig createDefault() {
        return new Builder().build();
    }

    /**
     * Creates a configuration with a specific initial timestamp.
     * Useful when loading data from saved state.
     */
    public static SimulationConfig withTimestamp(Instant timestamp) {
        return new Builder()
                .initialTimestamp(timestamp)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int speedupFactor = 3600;
        private int tickInterval = 50;
        private int durationInRealSeconds = 10;
        private Instant initialTimestamp = Instant.EPOCH;

        public Builder speedupFactor(int speedupFactor) {
            this.speedupFactor = speedupFactor;
            return this;
        }

        public Builder tickInterval(int tickInterval) {
            this.tickInterval = tickInterval;
            return this;
        }

        public Builder durationInRealSeconds(int durationInRealSeconds) {
            this.durationInRealSeconds = durationInRealSeconds;
            return this;
        }

        public Builder initialTimestamp(Instant initialTimestamp) {
            this.initialTimestamp = initialTimestamp != null ? initialTimestamp : Instant.EPOCH;
            return this;
        }

        public SimulationConfig build() {
            return new SimulationConfig(this);
        }
    }
}
