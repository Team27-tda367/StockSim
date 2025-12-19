package org.team27.stocksim.model.users;

import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.bot.IBotStrategy;
import org.team27.stocksim.model.users.bot.RandomStrategy;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents an automated trading agent in the simulation.
 *
 * <p>A Bot extends Trader and adds automated trading behavior through pluggable
 * strategies. Bots use a state machine (IDLE/ACTING) to ensure they only
 * execute one action at a time, preventing race conditions in concurrent
 * simulation environments.</p>
 *
 * <p><strong>Design Patterns:</strong> Strategy + State Machine</p>
 * <ul>
 *   <li>Pluggable trading strategies via IBotStrategy interface</li>
 *   <li>State machine prevents concurrent action execution</li>
 *   <li>Asynchronous action execution via BotActionExecutor</li>
 *   <li>Thread-safe state transitions</li>
 *   <li>Defaults to RandomStrategy if none specified</li>
 * </ul>
 *
 * <h2>State Machine:</h2>
 * <ul>
 *   <li>IDLE - Bot is waiting for next tick, can accept new actions</li>
 *   <li>ACTING - Bot is currently processing a trading action</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Create bot with specific strategy
 * IBotStrategy strategy = new MomentumTraderStrategy();
 * Bot bot = new Bot("bot1", "Momentum Bot", portfolio, strategy);
 *
 * // Bot automatically trades on each simulation tick
 * bot.tick(stockSim, botActionExecutor);
 *
 * // Check bot state
 * BotState state = bot.getState(); // IDLE or ACTING
 * BigDecimal balance = bot.getBalance();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Trader
 * @see User
 * @see IBotStrategy
 * @see BotState
 * @see BotActionExecutor
 * @see BotFactory
 */
public class Bot extends Trader {
    /**
     * Current state of the bot (IDLE or ACTING).
     */
    private BotState state = BotState.IDLE;

    /**
     * Trading strategy used to make decisions.
     */
    private IBotStrategy strategy;

    /**
     * Package-private constructor for use by BotFactory with specified strategy.
     *
     * @param id Unique identifier for the bot
     * @param name Display name of the bot
     * @param portfolio Bot's portfolio containing cash and positions
     * @param strategy Trading strategy to use for decision-making
     */
    Bot(String id, String name, Portfolio portfolio, IBotStrategy strategy) {
        super(id, name, portfolio);
        this.strategy = strategy;
    }

    Bot(String id, String name, Portfolio portfolio) {
        super(id, name, portfolio);
        this.strategy = new RandomStrategy();
    }

    public IBotStrategy getStrategy() {
        return strategy;
    }

    public void tick(StockSim model, BotActionExecutor executor) {
        if (!tryStartActing()) {
            return; // Already acting, so we skip this tick
        }

        List<Order> orders = strategy.decide(model, this);

        if (orders == null || orders.isEmpty()) {
            // No action needed, return to idle immediately
            returnToIdle();
        } else {
            BotAction action = new BotAction(this, orders, model);
            executor.submit(action);
        }
    }

    private synchronized boolean tryStartActing() {
        if (state == BotState.IDLE) {
            state = BotState.ACTING;
            return true;
        }
        return false;
    }

    public BigDecimal getBalance() {
        return this.getPortfolio().getBalance();
    }

    synchronized void returnToIdle() {
        state = BotState.IDLE;
    }

    public synchronized BotState getState() {
        return state;
    }
}
