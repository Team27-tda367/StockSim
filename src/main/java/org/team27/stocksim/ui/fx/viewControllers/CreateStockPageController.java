package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.ui.fx.MainViewAdapter;
import org.team27.stocksim.ui.fx.View;
import org.team27.stocksim.ui.fx.ViewSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreateStockPageController extends ViewController {

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

    public CreateStockPageController(SimController simController, MainViewAdapter viewAdapter) {
        super(simController, viewAdapter);
    }

    @FXML
    private void initialize() {
        // Bind the label to the adapter's message property
        createdStockLabel.textProperty().bind(viewAdapter.messageProperty());
    }

    @FXML
    private void createStock(ActionEvent event) {
        String symbol = inputSymbol.getText();
        String stockName = inputStockName.getText();
        String tickSize = inputTickSize.getText();
        String lotSize = inputLotSize.getText();

        // Delegate to controller
        simController.createStock(symbol, stockName, tickSize, lotSize);

        // Clear input fields after creation
        clearFields();
    }

    @FXML
    private void onMainView(ActionEvent event) {
        ViewSwitcher.switchTo(View.MAINVIEW);
    }

    private void clearFields() {
        inputSymbol.clear();
        inputStockName.clear();
        inputTickSize.clear();
        inputLotSize.clear();
    }
}
