package org.team27.stocksim.ui.fx;

import org.team27.stocksim.controller.SimController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Label;

public class MainViewController {
    private final SimController simController;

    public MainViewController(SimController simController) {
        this.simController = simController;
    }

    @FXML
    private Label outputLabel;

    @FXML
    private void initialize() {
        outputLabel.textProperty().bind(simController.messageProperty());}

    @FXML
    private void handleSampleAction(ActionEvent event) {
        simController.handleSampleAction();
    }

}