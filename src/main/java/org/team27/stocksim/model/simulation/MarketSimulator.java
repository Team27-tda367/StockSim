package org.team27.stocksim.model.simulation;

import org.team27.stocksim.model.clock.ClockProvider;
import org.team27.stocksim.model.clock.GameClock;
import org.team27.stocksim.model.clock.GameTicker;
import org.team27.stocksim.model.market.MarketState;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.users.Trader;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.function.Supplier;

public class MarketSimulator implements IMarketSimulator {

    private final Runnable onTick;
    private final int tickInterval;
    private final int speedupFactor;
    private MarketState state;
    private GameTicker ticker;
    private int totalTradesExecuted;
    private final Instant simulationStartTime;

    public MarketSimulator(Supplier<HashMap<String, Bot>> botsSupplier, Runnable onTick) {
        this(botsSupplier, onTick, null, 3600, 50, 10, Instant.EPOCH);
    }

    public MarketSimulator(Supplier<HashMap<String, Bot>> botsSupplier, Runnable onTick, Runnable onSaveData) {
        this(botsSupplier, onTick, onSaveData, 3600, 50, 10, Instant.EPOCH);
    }

    public MarketSimulator(Supplier<HashMap<String, Bot>> botsSupplier, Runnable onTick, Runnable onSaveData,
            int speedupFactor, int tickInterval, int durationInRealSeconds, Instant initialTimeStamp) {
        this.state = MarketState.PAUSED;
        this.onTick = onTick;
        this.speedupFactor = speedupFactor;
        this.tickInterval = tickInterval;
        this.totalTradesExecuted = 0;
        this.simulationStartTime = initialTimeStamp;
    }

    @Override
    public void start() {
        state = MarketState.RUNNING;

        GameClock clock = new GameClock(
                ZoneId.systemDefault(),
                simulationStartTime,
                speedupFactor);

        ticker = new GameTicker(clock, simInstant -> tick(), tickInterval);
        ticker.start();

    }

    @Override
    public void pause() {
        state = MarketState.PAUSED;
    }

    @Override
    public void stop() {
        state = MarketState.PAUSED;

        if (ticker != null) {
            ticker.stop();
        }
    }

    private void tick() {
        if (state != MarketState.RUNNING) {
            return;
        }

        if (onTick != null) {
            onTick.run();
        }
    }

    @Override
    public MarketState getState() {
        return state;
    }

    @Override
    public void setTotalTradesExecuted(int count) {
        this.totalTradesExecuted = count;
    }
}
