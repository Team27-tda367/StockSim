package org.team27.stocksim.controller;

import org.team27.stocksim.model.StockSim;

public interface ISimController {
    void createStock(String symbol, String stockName, String tickSize, String lotSize);

    StockSim getModel();

}
