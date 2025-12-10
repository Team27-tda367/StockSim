package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

public class UserFactory implements ITraderFactory {
    @Override
    public Trader createTrader(String id, String name, Portfolio portfolio) {
        return new User(id, name, portfolio);
    }
}
