package org.team27.stocksim.controller;

import java.math.BigDecimal;
import java.util.HashMap;

import org.team27.stocksim.model.SimSetup;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Instrument;
import org.team27.stocksim.observer.ModelObserver;

public class SimController implements ISimController {

    private StockSim model;

    public SimController(StockSim model) {
        this.model = model;

    }

    public void setUpSimulation() {
        SimSetup setup = new SimSetup(model);
        setup.start();
    }

    public void createStock(String symbol, String stockName, String tickSize, String lotSize) {
        model.createStock(symbol, stockName, tickSize, lotSize);
    }

    public void addObserver(ModelObserver obs) {
        model.addObserver(obs);
    }

    public void removeObserver(ModelObserver obs) {
        model.removeObserver(obs);
    }

    @Override
    public HashMap<String, Instrument> getAllStocks() {
        return model.getStocks();
    }

}