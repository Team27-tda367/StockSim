package org.team27.stocksim.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.team27.stocksim.model.market.StockSim;

public class Controller {

    private StockSim model;

    public void setModel(StockSim model) {
        this.model = model;
        System.out.println("Model has been set successfully");
    }

    @FXML
    private Label outputLabel;

    @FXML
    private void handleSampleAction(ActionEvent event) {
        String testText = model.testFetch();
        outputLabel.setText(testText);
    }

}