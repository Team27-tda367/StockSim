package org.team27.stocksim.model.clock;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GameTicker {

    private final GameClock clock;
    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

    private long lastSecond;
    private Consumer<Instant> onSecondHandler;

    public GameTicker(GameClock clock, Consumer<Instant> onSecondHandler) {
        this.clock = clock;
        this.onSecondHandler = onSecondHandler;
    }

    public void start() {
        lastSecond = clock.instant().getEpochSecond();
        exec.scheduleAtFixedRate(this::tick, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        Instant nowSim = clock.instant();
        long currentSec = nowSim.getEpochSecond();

        while (lastSecond < currentSec) {
            lastSecond++;
            onSimulatedSecond(Instant.ofEpochSecond(lastSecond));
        }
    }

    private void onSimulatedSecond(Instant simSecond) {
        // Call-backen – här händer magin:
        this.onSecondHandler.accept(simSecond);
    }

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

    public void setCallback(Consumer<Instant> onSecondHandler) {
        this.onSecondHandler = onSecondHandler;

    }
}
