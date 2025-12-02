package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.ui.fx.EView;
import org.team27.stocksim.ui.fx.ViewSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainViewController {

    @FXML
    private void initialize() {
        /*
         * Initialize all bindings
         */
        // TODO: Add bindings when needed to the viewAdapter
    }

    @FXML
    public void onExample(ActionEvent event) {
        ViewSwitcher.switchTo(EView.CREATESTOCK);
    }

    // Funktion för att koppla sorteringen i stockTags
    @FXML
    private void sortByTag(ActionEvent event) {
        // TODO: Implement sorting logic - send call to simController if needed
    }

    @FXML
    public void onMainView(ActionEvent event) {
        ViewSwitcher.switchTo(EView.MAINVIEW);
    }

    @FXML
    public void onStockView(ActionEvent event) {
        ViewSwitcher.switchTo(EView.STOCKVIEW);
    }

}
