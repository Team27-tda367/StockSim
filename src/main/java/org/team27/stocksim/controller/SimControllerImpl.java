package org.team27.stocksim.controller;

import org.team27.stocksim.model.market.StockSim;

import javafx.beans.property.StringProperty;

public class SimControllerImpl implements SimController {

    private StockSim model;

    public SimControllerImpl(StockSim model) {
        this.model = model;
    }

    @Override
    public void handleSampleAction() {
        System.out.println("SimControllerImpl: handleSampleAction called");
        model.testFetch();
    }

    @Override
    public StringProperty messageProperty() {
        // Bara vidarebefordrar modellens property
        return model.messageProperty();
    }

}