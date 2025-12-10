package org.team27.stocksim.model.simulation;

import org.team27.stocksim.model.clock.GameClock;
import org.team27.stocksim.model.clock.GameTicker;
import org.team27.stocksim.model.market.MarketState;
import org.team27.stocksim.model.users.Trader;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.function.Supplier;


public class MarketSimulator {

    private final Supplier<HashMap<String, Trader>> botsSupplier;
    private final Runnable onTick;
    private MarketState state;
    private GameTicker ticker;
    private int totalTradesExecuted;

    public MarketSimulator(Supplier<HashMap<String, Trader>> botsSupplier, Runnable onTick) {
        this.state = MarketState.PAUSED;
        this.botsSupplier = botsSupplier;
        this.onTick = onTick;
        this.totalTradesExecuted = 0;
    }

    public void start() {
        state = MarketState.RUNNING;

        GameClock clock = new GameClock(
                ZoneId.of("Europe/Stockholm"),
                Instant.now(),
                10000
        );

        ticker = new GameTicker(clock, simInstant -> tick());
        ticker.start();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                clock.setSpeed(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        System.out.println("Market simulation started");
    }

    public void pause() {
        state = MarketState.PAUSED;
        System.out.println("Market simulation paused");
    }


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

    public MarketState getState() {
        return state;
    }

    public void setTotalTradesExecuted(int count) {
        this.totalTradesExecuted = count;
    }
}

