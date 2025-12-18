package org.team27.stocksim.model.simulation;

import org.team27.stocksim.model.market.MarketState;

public interface IMarketSimulator {

    void start();

    void pause();

    void stop();

    MarketState getState();

    void setTotalTradesExecuted(int count);
}
