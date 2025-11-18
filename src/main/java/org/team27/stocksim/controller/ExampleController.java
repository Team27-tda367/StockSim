package org.team27.stocksim.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.team27.stocksim.market.Stock;

public class ExampleController {

    @FXML
    private Label outputLabel;

    @FXML
    private void handleSampleAction(ActionEvent event) {
        // Create a model object and call a sample method
        Stock s = new Stock("ABC", "Test Company", 0.01, 100);
        String name = s.getName();

        // Show result in the UI and console
        outputLabel.setText("Model returned: " + name);
        System.out.println("Model returned: " + name);
    }

}