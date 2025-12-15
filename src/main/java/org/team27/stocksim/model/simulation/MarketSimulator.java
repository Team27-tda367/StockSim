package org.team27.stocksim.model.simulation;

import org.team27.stocksim.model.clock.ClockProvider;
import org.team27.stocksim.model.clock.GameClock;
import org.team27.stocksim.model.clock.GameTicker;
import org.team27.stocksim.model.market.MarketState;
import org.team27.stocksim.model.users.Trader;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.function.Supplier;

public class MarketSimulator implements IMarketSimulator {

    private final Supplier<HashMap<String, Trader>> botsSupplier;
    private final Runnable onTick;
    private final Runnable onSaveData;
    private MarketState state;
    private GameTicker ticker;
    private int totalTradesExecuted;

    public MarketSimulator(Supplier<HashMap<String, Trader>> botsSupplier, Runnable onTick) {
        this(botsSupplier, onTick, null);
    }

    public MarketSimulator(Supplier<HashMap<String, Trader>> botsSupplier, Runnable onTick, Runnable onSaveData) {
        this.state = MarketState.PAUSED;
        this.botsSupplier = botsSupplier;
        this.onTick = onTick;
        this.onSaveData = onSaveData;
        this.totalTradesExecuted = 0;
    }

    @Override
    public void start() {
        state = MarketState.RUNNING;

        GameClock clock = new GameClock(
                ZoneId.of("Europe/Stockholm"),
                Instant.now(),
                3600);

        // Set the game clock globally so all components use simulation time
        ClockProvider.setClock(clock);

        ticker = new GameTicker(clock, simInstant -> tick());
        ticker.start();

        new Thread(() -> {
            try {
                Thread.sleep(10000); // Let the simulation run for 10 seconds

                clock.setSpeed(5);
                // Save stock prices to database
                if (onSaveData != null) {
                    onSaveData.run();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        System.out.println("Market simulation started");
    }

    @Override
    public void pause() {
        state = MarketState.PAUSED;
        System.out.println("Market simulation paused");
    }

    @Override
    public void stop() {
        state = MarketState.PAUSED;

        if (ticker != null) {
            ticker.stop();
        }

        System.out.println("\n========================================");
        System.out.println("Simulation stopped");
        System.out.println("Total trades executed: " + totalTradesExecuted);
        System.out.println("========================================\n");
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
