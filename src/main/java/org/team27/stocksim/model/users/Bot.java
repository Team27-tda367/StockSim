package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

public class Bot extends Trader{
    public Bot(String id, String name, Portfolio portfolio) {
        super(id, name, portfolio);
    }
}
