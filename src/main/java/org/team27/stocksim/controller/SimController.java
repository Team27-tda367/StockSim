package org.team27.stocksim.controller;

public interface SimController {

    void createStock(String symbol, String stockName, String tickSize, String lotSize);

    // All other method signatures that calls the model layer

}
