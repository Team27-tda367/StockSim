package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;
import static org.team27.stocksim.model.util.MoneyUtils.money;

public class UserFactory implements TraderFactory {
    @Override
    public Trader createTrader(String id, String name) {
        Portfolio portfolio = new Portfolio(money("10000"));
        return new User(id, name, portfolio);
    }
}
