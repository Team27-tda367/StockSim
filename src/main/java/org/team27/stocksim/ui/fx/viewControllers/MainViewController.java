package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.ui.fx.MainViewAdapter;
import org.team27.stocksim.ui.fx.View;
import org.team27.stocksim.ui.fx.ViewSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainViewController extends ViewController {

    public MainViewController(SimController simController, MainViewAdapter viewAdapter) {
        super(simController, viewAdapter);
    }

    @FXML
    private void initialize() {
        /*
         * Initialize all bindings
         */
        // TODO: Add bindings when needed to the viewAdapter
    }

    @FXML
    public void onExample(ActionEvent event) {
        ViewSwitcher.switchTo(View.CREATESTOCK);
    }

    // Funktion för att koppla sorteringen i stockTags
    @FXML
    private void sortByTag(ActionEvent event) {
        // TODO: Implement sorting logic - send call to simController if needed
    }

    @FXML
    public void onMainView(ActionEvent event) {
        ViewSwitcher.switchTo(View.MAINVIEW);
    }

    @FXML
    public void onStockView(ActionEvent event) {
        ViewSwitcher.switchTo(View.STOCKVIEW);
    }

}
