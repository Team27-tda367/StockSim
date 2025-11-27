package org.team27.stocksim.model.users;

import org.team27.stocksim.model.users.bot.BotStrategy;
import org.team27.stocksim.model.users.bot.RandomStrategy;

public class Bot extends Trader {
    private BotStrategy strategy;

    public Bot(String id) {
        this(id, new RandomStrategy());
        this.strategy = new RandomStrategy();
    }

    public Bot(String id, BotStrategy strategy) {
        super(id);
        this.strategy = strategy;
    }

    public BotStrategy getStrategy() {
        return strategy;
    }
}
