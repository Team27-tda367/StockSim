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

/**
 * Controls the lifecycle and timing of market simulation.
 *
 * <p>MarketSimulator manages the time-accelerated execution of the trading
 * simulation. It creates and manages the GameClock and GameTicker, coordinates
 * simulation state (running/paused/stopped), and triggers periodic tick callbacks
 * where bot trading decisions occur.</p>
 *
 * <p><strong>Design Patterns:</strong> Facade + State Machine + Observer</p>
 * <ul>
 *   <li>Manages simulation lifecycle (start, pause, stop)</li>
 *   <li>Creates time-accelerated clock for simulation</li>
 *   <li>Triggers periodic tick callbacks for bot actions</li>
 *   <li>Sets global clock via ClockProvider</li>
 *   <li>Tracks simulation state and metrics</li>
 * </ul>
 *
 * <h2>Simulation States:</h2>
 * <ul>
 *   <li><strong>PAUSED:</strong> Initial state, simulation not running</li>
 *   <li><strong>RUNNING:</strong> Active simulation, ticks executing</li>
 * </ul>
 *
 * <h2>Typical Configuration:</h2>
 * <ul>
 *   <li>Speed: 3600x (1 simulated hour per real second)</li>
 *   <li>Tick interval: 50ms real-time</li>
 *   <li>Each tick: Multiple simulated seconds may pass</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * MarketSimulator simulator = new MarketSimulator(
 *     () -> traderRegistry.getBots(),
 *     () -> {
 *         // Execute bot trading on each tick
 *         for (Bot bot : bots.values()) {
 *             bot.tick(stockSim, executor);
 *         }
 *     },
 *     3600,  // 3600x speed
 *     50,    // 50ms tick interval
 *     60     // Run for 60 real seconds
 * );
 *
 * simulator.start();
 * // ... simulation runs
 * simulator.pause();
 * simulator.start();
 * simulator.stop();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see IMarketSimulator
 * @see GameClock
 * @see GameTicker
 * @see MarketState
 */
public class MarketSimulator implements IMarketSimulator {

    /**
     * Callback invoked on each simulation tick.
     */
    private final Runnable onTick;

    /**
     * Real-time milliseconds between ticks.
     */
    private final int tickInterval;

    /**
     * Time acceleration factor (e.g., 3600 = 1 real second = 1 simulated hour).
     */
    private final int speedupFactor;

    /**
     * Current simulation state.
     */
    private MarketState state;

    /**
     * Ticker generating periodic callbacks.
     */
    private GameTicker ticker;

    /**
     * Counter for total trades executed (for statistics).
     */
    private int totalTradesExecuted;

    /**
     * Accelerated clock for simulation time.
     */
    private GameClock clock;

    /**
     * Constructs a MarketSimulator with default parameters.
     *
     * @param botsSupplier Supplier for bot map (currently unused)
     * @param onTick Callback invoked on each tick
     */
    public MarketSimulator(Supplier<HashMap<String, Bot>> botsSupplier, Runnable onTick) {
        this(botsSupplier, onTick, null, 3600, 50, 10);
    }

    /**
     * Constructs a MarketSimulator with save callback.
     *
     * @param botsSupplier Supplier for bot map
     * @param onTick Callback invoked on each tick
     * @param onSaveData Callback for saving data (currently unused)
     */
    public MarketSimulator(Supplier<HashMap<String, Bot>> botsSupplier, Runnable onTick, Runnable onSaveData) {
        this(botsSupplier, onTick, onSaveData, 3600, 50, 10);
    }

    /**
     * Constructs a MarketSimulator with full configuration.
     *
     * @param botsSupplier Supplier for bot map
     * @param onTick Callback invoked on each tick
     * @param onSaveData Callback for saving data
     * @param speedupFactor Time acceleration multiplier
     * @param tickInterval Milliseconds between ticks
     * @param durationInRealSeconds Simulation duration (currently unused)
     */
    public MarketSimulator(Supplier<HashMap<String, Bot>> botsSupplier, Runnable onTick, Runnable onSaveData,
            int speedupFactor, int tickInterval, int durationInRealSeconds) {
        this.state = MarketState.PAUSED;
        this.onTick = onTick;
        this.speedupFactor = speedupFactor;
        this.tickInterval = tickInterval;
        this.totalTradesExecuted = 0;
    }

    /**
     * Starts the simulation.
     *
     * <p>Creates the game clock, sets it globally via ClockProvider,
     * and starts the ticker. Transitions to RUNNING state.</p>
     */
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

    /**
     * Pauses the simulation.
     *
     * <p>Transitions to PAUSED state. Ticker continues but tick()
     * returns immediately without executing logic.</p>
     */
    @Override
    public void pause() {
        state = MarketState.PAUSED;
    }

    /**
     * Stops the simulation permanently.
     *
     * <p>Stops the ticker and transitions to PAUSED state.
     * To resume, must call start() again.</p>
     */
    @Override
    public void stop() {
        state = MarketState.PAUSED;

        if (ticker != null) {
            ticker.stop();
        }
    }

    /**
     * Executes one simulation tick.
     *
     * <p>Called periodically by ticker. Only executes onTick callback
     * if simulation is in RUNNING state.</p>
     */
    private void tick() {
        if (state != MarketState.RUNNING) {
            return;
        }

        if (onTick != null) {
            onTick.run();
        }
    }

    /**
     * Gets the current simulation state.
     *
     * @return Current MarketState (RUNNING or PAUSED)
     */
    @Override
    public MarketState getState() {
        return state;
    }

    /**
     * Updates the total trades executed counter.
     *
     * @param count New trade count
     */
    @Override
    public void setTotalTradesExecuted(int count) {
        this.totalTradesExecuted = count;
    }
}
