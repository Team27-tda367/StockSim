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
    private GameClock clock;

    public MarketSimulator(Supplier<HashMap<String, Bot>> botsSupplier, Runnable onTick) {
        this(botsSupplier, onTick, null, 3600, 50, 10);
    }

    public MarketSimulator(Supplier<HashMap<String, Bot>> botsSupplier, Runnable onTick, Runnable onSaveData) {
        this(botsSupplier, onTick, onSaveData, 3600, 50, 10);
    }

    public MarketSimulator(Supplier<HashMap<String, Bot>> botsSupplier, Runnable onTick, Runnable onSaveData,
            int speedupFactor, int tickInterval, int durationInRealSeconds) {
        this.state = MarketState.PAUSED;
        this.onTick = onTick;
        this.speedupFactor = speedupFactor;
        this.tickInterval = tickInterval;
        this.totalTradesExecuted = 0;
    }

    @Override
    public void start() {
        state = MarketState.RUNNING;

        clock = new GameClock(
                ZoneId.of("Europe/Stockholm"),
                Instant.EPOCH,
                speedupFactor);

        // Set the game clock globally so all components use simulation time
        ClockProvider.setClock(clock);

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
