package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.view.fx.EView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.team27.stocksim.view.ViewAdapter;

import java.math.BigDecimal;
import java.util.Map;

public class PortfolioViewController extends ViewControllerBase implements ViewAdapter.PortfolioChangedListener {

    @FXML
    private Label availableBalanceLabel;

    @FXML
    private ListView<String> holdingsListView; // Add this to your FXML if not present

    private ObservableList<String> holdingsList = FXCollections.observableArrayList();

    @Override
    protected void onInit() {
        // Register for portfolio change events
        viewAdapter.addPortfolioChangedListener(this);

        // Set up ListView if it exists
        if (holdingsListView != null) {
            holdingsListView.setItems(holdingsList);
        }

        // Initial display
        updatePortfolioDisplay();
    }

    @Override
    public void onPortfolioChanged() {
        // Update UI on JavaFX thread when portfolio changes
        Platform.runLater(this::updatePortfolioDisplay);
    }

    /**
     * Updates the portfolio display with current balance and holdings
     */
    private void updatePortfolioDisplay() {
        User user = modelController.getUser();
        Portfolio portfolio = user.getPortfolio();

        // Update balance
        BigDecimal balance = portfolio.getBalance();
        availableBalanceLabel.setText("Balance: $" + String.format("%.2f", balance));

        // Update stock holdings
        Map<String, Integer> holdings = portfolio.getStockHoldings();

        // Clear and rebuild the holdings list
        holdingsList.clear();

        if (holdings.isEmpty()) {
            holdingsList.add("No stocks owned");
        } else {
            // Add each stock holding to the list
            holdings.forEach((symbol, quantity) -> holdingsList.add(String.format("%s: %d shares", symbol, quantity)));
        }

        holdings.forEach((symbol, quantity) -> System.out.println(symbol + ": " + quantity + " shares"));
    }

    @FXML
    public void onMainView(ActionEvent event) {
        viewSwitcher.switchTo(EView.MAINVIEW);
    }

}
