package org.team27.stocksim.model.users;

import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.bot.IBotStrategy;
import org.team27.stocksim.model.users.bot.RandomStrategy;

import java.math.BigDecimal;
import java.util.List;

public class Bot extends Trader {
    private BotState state = BotState.IDLE;
    private IBotStrategy strategy;

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
