package org.team27.stocksim.ui.fx;

import org.team27.stocksim.controller.SimController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Label;

public class MainViewController {
    private final SimController simController;
    private final MainViewAdapter viewModel;

    public MainViewController(SimController simController, MainViewAdapter viewModel) {
        this.simController = simController;
        this.viewModel = viewModel;
    }

    @FXML
    private Label outputLabel;

    @FXML
    private void handleSampleAction(ActionEvent event) {
        simController.handleSampleAction();
    }

    @FXML
    private void initialize() {
        /*
         * Initialize all bindings
         */
        outputLabel.textProperty().bind(viewModel.messageProperty());
    }
}