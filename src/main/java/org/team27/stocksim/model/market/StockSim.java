package org.team27.stocksim.model.market;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StockSim {
    MarketState state;

    private final StringProperty message = new SimpleStringProperty("");

    public StringProperty messageProperty() {
        return message;
    }

    public StockSim() {
        System.out.println("Succesfully created Sim-model");
    }

    public void testFetch() {
        String testString = "Test string from model";
        System.out.println("Model testFetch called, setting message to: " + testString);
        message.set(testString);
    }

}
