package org.team27.stocksim.ui.fx.viewControllers;

import java.util.HashMap;

import javax.sound.midi.Instrument;

import org.team27.stocksim.observer.ModelEvent;
import org.team27.stocksim.observer.ModelObserver;
import org.team27.stocksim.ui.fx.EView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreateStockPageController extends ViewControllerBase implements ModelObserver {

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

    @Override
    protected void onInit() {
        modelController.addObserver(this);
    }

    @FXML
    private void createStock(ActionEvent event) {
        String symbol = inputSymbol.getText();
        String stockName = inputStockName.getText();
        String tickSize = inputTickSize.getText();
        String lotSize = inputLotSize.getText();

        modelController.createStock(symbol, stockName, tickSize, lotSize);
        // Clear input fields after creation
        clearFields();
    }

    @FXML
    private void onMainView(ActionEvent event) {
        viewSwitcher.switchTo(EView.MAINVIEW);
    }

    private void clearFields() {
        inputSymbol.clear();
        inputStockName.clear();
        inputTickSize.clear();
        inputLotSize.clear();
    }

    @Override
    public void modelChanged(ModelEvent event) {
        System.out.println("Model changed");
        if (event.getType() == ModelEvent.Type.STOCKS_CHANGED) {
            HashMap<String, Instrument> stocks = (HashMap<String, Instrument>) event.getPayload();
            createdStockLabel.setText("Stock created: " + stocks.toString());
            // uppdatera UI / view-modell
        }
    }
}
