package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.ui.fx.EView;
import org.team27.stocksim.ui.fx.ViewSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StockViewController {

    @FXML
    private Label favoriteIcon;

    private boolean isFavorite = false;

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
        ViewSwitcher.switchTo(EView.MAINVIEW);
    }

    @FXML
    private void onToggleFavorite() {
        isFavorite = !isFavorite;
        favoriteIcon.setText(isFavorite ? "★" : "☆");
    }

}
