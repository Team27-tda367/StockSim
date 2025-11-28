package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

public class User extends Trader {
    public User(String id, String name, Portfolio portfolio) {
        super(id, name, portfolio);
    }
}
