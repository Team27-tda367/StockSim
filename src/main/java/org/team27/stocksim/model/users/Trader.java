package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

public abstract class Trader {

    private final String id;
    private final String displayName;
    private Portfolio portfolio;

    public Trader(String id, String name, Portfolio portfolio) {
        this.id = id;
        this.displayName = name;
        this.portfolio = portfolio;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

}
