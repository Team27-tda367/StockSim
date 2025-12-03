package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.observer.ModelEvent;
import org.team27.stocksim.ui.fx.EView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StockViewController extends ViewControllerBase {

    @FXML
    private Label favoriteIcon;
    private boolean isFavorite = false;

    /**
     * Handler for the header "Home" button (FXML references `onExample`).
     * Navigates back to the main view.
     */
    @FXML
    public void onExample(ActionEvent event) {
        viewSwitcher.switchTo(EView.MAINVIEW);
    }

    @FXML
    private void onToggleFavorite() {
        isFavorite = !isFavorite;
        favoriteIcon.setText(isFavorite ? "★" : "☆");
    }

    @Override
    public void modelChanged(ModelEvent event) {
        System.out.println("Stockview changed");
    }

    @Override
    protected void onInit() {
        modelController.getModel().addObserver(this);
    }

}
