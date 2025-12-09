package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.view.fx.EView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreateStockPageController extends ViewControllerBase {

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
    }

    @FXML
    private void createStock(ActionEvent event) {
        String symbol = inputSymbol.getText();
        String stockName = inputStockName.getText();
        String tickSize = inputTickSize.getText();
        String lotSize = inputLotSize.getText();
        String category = "Technology"; // placeholder

        modelController.createStock(symbol, stockName, tickSize, lotSize, category);
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

    // Event handlers can be added here if needed
    // Example:
    // @Override
    // public void onStocksChanged(Object payload) {
    //     createdStockLabel.setText((String) payload);
    // }

}
