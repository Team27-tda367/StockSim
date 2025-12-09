package org.team27.stocksim.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.OrderHistory;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.observer.ModelObserver;

public interface ISimController {
    void createStock(String symbol, String stockName, String tickSize, String lotSize, String category);

    void addObserver(ModelObserver obs);

    void removeObserver(ModelObserver obs);

    ArrayList<String> getAllCategories();

    HashMap<String, Instrument> getStocks(String category);

    void setUpSimulation();

    User getUser();

    void buyStock(String stockSymbol, int quantity, BigDecimal price);

    void sellStock(String stockSymbol, int quantity, BigDecimal price);

    OrderHistory getOrderHistory();
}
