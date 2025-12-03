package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.observer.ModelEvent;
import org.team27.stocksim.ui.fx.EView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainViewController extends ViewControllerBase {

    @Override
    protected void onInit() {
        modelController.addObserver(this);
    }

    @FXML
    public void onExample(ActionEvent event) {
        viewSwitcher.switchTo(EView.CREATESTOCK);
    }

    // Funktion för att koppla sorteringen i stockTags
    @FXML
    private void sortByTag(ActionEvent event) {
        // TODO: Implement sorting logic - send call to simController if needed
    }

    @FXML
    public void onMainView(ActionEvent event) {
        viewSwitcher.switchTo(EView.MAINVIEW);
    }

    @FXML
    public void onStockView(ActionEvent event) {
        viewSwitcher.switchTo(EView.STOCKVIEW);
    }

    @Override
    public void modelChanged(ModelEvent event) {
        System.out.println("Model changed in MainViewController");
    }
}
