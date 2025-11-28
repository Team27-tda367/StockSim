package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

public interface TraderFactory {
    Trader createTrader(String id, String name, Portfolio portfolio);
}
