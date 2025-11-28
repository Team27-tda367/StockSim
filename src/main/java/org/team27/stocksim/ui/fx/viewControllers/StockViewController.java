package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.ui.fx.MainViewAdapter;
import org.team27.stocksim.ui.fx.View;
import org.team27.stocksim.ui.fx.ViewSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class StockViewController extends ViewController {

    public StockViewController(SimController simController, MainViewAdapter viewAdapter) {
        super(simController, viewAdapter);
    }

    @FXML
    private void initialize() {
        // Initialize bindings or UI setup here if needed.
    }

    /**
     * Handler for the header "Home" button (FXML references `onExample`).
     * Navigates back to the main view.
     */
    @FXML
    public void onExample(ActionEvent event) {
        ViewSwitcher.switchTo(View.MAINVIEW);
    }

}
