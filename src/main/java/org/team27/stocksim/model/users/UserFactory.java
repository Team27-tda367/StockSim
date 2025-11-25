package org.team27.stocksim.model.users;

public class UserFactory implements TraderFactory{
    @Override
    public Trader createTrader(String id) {
        return new User(id);
    }
}
