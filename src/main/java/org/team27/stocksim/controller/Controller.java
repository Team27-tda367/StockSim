package org.team27.stocksim.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.team27.stocksim.model.market.StockSim;

public class Controller {

    private StockSim model;

    public void setModel(StockSim model) {
        this.model = model;

        model.messageProperty().addListener((obs, oldVal, newVal) -> {
            outputLabel.setText(newVal);
        });
        model.messageCreatedStock().addListener((obs, oldVal, newVal) -> {
            createdStockLabel.setText(newVal);
        });

    }

    @FXML
    private Label outputLabel;

    @FXML
    private void handleSampleAction(ActionEvent event) {
        model.testFetch();
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
        model.createStock(symbol, stockName, tickSize, lotSize);
    }
    @FXML
    public void onMainView(ActionEvent event){
        
        ViewSwitcher.switchTo(View.MAINVIEW);

    }
}