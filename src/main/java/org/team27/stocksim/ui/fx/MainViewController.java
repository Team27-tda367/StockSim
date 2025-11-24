package org.team27.stocksim.ui.fx;

import org.team27.stocksim.controller.SimController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainViewController {
    private final SimController simController;
    private final MainViewAdapter viewAdapter;

    public MainViewController(SimController simController, MainViewAdapter viewAdapter) {
        this.simController = simController;
        this.viewAdapter = viewAdapter;
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
        outputLabel.textProperty().bind(viewAdapter.messageProperty());

        /*
         * model.messageProperty().addListener((obs, oldVal, newVal) -> {
         * outputLabel.setText(newVal);
         * });
         * model.messageCreatedStock().addListener((obs, oldVal, newVal) -> {
         * createdStockLabel.setText(newVal);
         * });
         */
    }

    @FXML
    private TextField inputSymbol;
    @FXML
    private TextField inputStockName;
    @FXML
    private TextField inputTickSize;
    @FXML
    private TextField inputLotSize;

    @FXML
    private Label createdStockLabel;

    @FXML
    private void createStock(ActionEvent event) {
        String symbol = inputSymbol.getText();
        String stockName = inputStockName.getText();
        String tickSize = inputTickSize.getText();
        String lotSize = inputLotSize.getText();
        simController.createStock(symbol, stockName, tickSize, lotSize);
    }
}