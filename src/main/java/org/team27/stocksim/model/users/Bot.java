package org.team27.stocksim.model.users;

import org.team27.stocksim.model.users.bot.BotStrategy;
import org.team27.stocksim.model.users.bot.RandomStrategy;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.portfolio.Portfolio;

public class Bot extends Trader {
    private BotStrategy strategy;

    Bot(String id, String name, Portfolio portfolio) {
        super(id, name, portfolio);
        this.strategy = new RandomStrategy();
    }

    public BotStrategy getStrategy() {
        return strategy;
    }

    public void decide(StockSim model) {
        strategy.decide(model, this);
    }
}
