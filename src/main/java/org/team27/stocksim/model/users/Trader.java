package org.team27.stocksim.model.users;

public abstract class Trader {

    protected final String id;
    protected final double startingBalance;
    protected final double balance;

    public Trader(String id) {
        startingBalance = 10000;

        this.id = id;
        this.balance = startingBalance;
        // create Portfolio here
    }

}
