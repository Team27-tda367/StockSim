package org.team27.stocksim.controller;

import org.team27.stocksim.observer.ModelObserver;

public interface ISimController {
    void createStock(String symbol, String stockName, String tickSize, String lotSize);

    void addObserver(ModelObserver obs);

    void removeObserver(ModelObserver obs);

}
