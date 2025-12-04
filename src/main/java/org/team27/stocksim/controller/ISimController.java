package org.team27.stocksim.controller;

import java.util.HashMap;

import org.team27.stocksim.model.market.Instrument;
import org.team27.stocksim.observer.ModelObserver;

public interface ISimController {
    void createStock(String symbol, String stockName, String tickSize, String lotSize);

    void addObserver(ModelObserver obs);

    void removeObserver(ModelObserver obs);

    HashMap<String, Instrument> getAllStocks();

    void setUpSimulation();
}
