package org.team27.stocksim.model.market;

public interface MatchingStrategy {
    void match(OrderBook book);
}
