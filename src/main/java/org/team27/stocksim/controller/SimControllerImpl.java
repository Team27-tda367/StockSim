package org.team27.stocksim.controller;

import org.team27.stocksim.model.market.StockSim;

public class SimControllerImpl implements SimController {
    private StockSim model;

    public SimControllerImpl(StockSim model) {
        this.model = model;
    }


    @Override
    public void createStock(String symbol, String stockName, String tickSize, String lotSize) {
        model.createStock(symbol, stockName, tickSize, lotSize);
    }

}