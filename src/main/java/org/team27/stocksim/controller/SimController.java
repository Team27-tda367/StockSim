package org.team27.stocksim.controller;

public interface SimController {

    // Define any methods that SimController should have
    void handleSampleAction();

    void createStock(String symbol, String stockName, String tickSize, String lotSize);

}
