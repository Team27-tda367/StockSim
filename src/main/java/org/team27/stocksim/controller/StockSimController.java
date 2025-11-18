package org.team27.stocksim.controller;

import org.team27.stocksim.model.market.StockSim;

public class StockSimController {

    private StockSim model;

    public StockSimController(StockSim model) {
        this.model = model;
        System.out.println("Succesfully created Controller");
    }

}
