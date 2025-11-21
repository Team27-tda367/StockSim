package org.team27.stocksim.model.market;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;

public class StockSim {
    MarketState state;
    HashMap<String, Instrument> stocks;
    InstrumentFactory stockFactory;

    private final StringProperty message = new SimpleStringProperty("");
    public StringProperty messageProperty() {
        return message;
    }

    private final StringProperty createdStockMsg = new SimpleStringProperty("");
    public StringProperty messageCreatedStock() {
        return createdStockMsg;
    }

    public StockSim() {
        // Stock related inits
        stocks = new HashMap<>();
        stockFactory = new StockFactory();

        System.out.println("Succesfully created Sim-model");
    }

    public void testFetch() {
        String testString = "Test string from model";
        message.set(testString);
    }

    public void createStock(String symbol, String stockName, String tickSize, String lotSize){
        // checking if symbol already exists (if yes -> error)
        if (stocks.containsKey(symbol)) {
            createdStockMsg.set("Symbol already exists!");
        } else {
            Instrument stock = stockFactory.createInstrument(symbol, stockName, Double.parseDouble(tickSize), Integer.parseInt(lotSize));
            stocks.put(stockName, stock); //should be unique-symbol, not stockName
            String createdStock = symbol + " " + stockName + " " + tickSize + " " + lotSize;
            createdStockMsg.set(createdStock);
        }

        System.out.println(stocks); // test-print
    }

}
