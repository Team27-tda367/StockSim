package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

public abstract class Trader {

    protected final String id;
    protected final String displayName;
    protected Portfolio portfolio;

    public Trader(String id, String name, Portfolio portfolio) {
        this.id = id;
        this.displayName = name;
        this.portfolio = portfolio;
    }

    public String getId() {
        return id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

}
