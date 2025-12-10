package org.team27.stocksim.model.users;

import org.team27.stocksim.model.users.bot.IBotStrategy;
import org.team27.stocksim.model.users.bot.RandomStrategy;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.portfolio.Portfolio;

import java.util.List;

public class Bot extends Trader {
    private IBotStrategy strategy;

    Bot(String id, String name, Portfolio portfolio) {
        super(id, name, portfolio);
        this.strategy = new RandomStrategy();
    }

    public IBotStrategy getStrategy() {
        return strategy;
    }

    public void decide(StockSim model) {
        List<Order> orders = strategy.decide(model, this);
        for (Order order : orders) {
            model.placeOrder(order);
        }
    }
}
