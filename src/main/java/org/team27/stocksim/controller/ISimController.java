package org.team27.stocksim.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.observer.ModelObserver;

public interface ISimController {
    void createStock(String symbol, String stockName, String tickSize, String lotSize);

    void addObserver(ModelObserver obs);

    void removeObserver(ModelObserver obs);

    ArrayList<String> getAllCategories();

    HashMap<String, Instrument> getAllStocks();

    void setUpSimulation();

    User getUser();

    void buyStock(String stockSymbol, int quantity);
}
