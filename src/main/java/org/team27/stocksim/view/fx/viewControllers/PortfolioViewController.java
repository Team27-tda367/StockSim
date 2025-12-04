package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.observer.ModelEvent;
import org.team27.stocksim.view.fx.EView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PortfolioViewController extends ViewControllerBase {
    @Override
    protected void onInit() {
        // Initialization code for PortfolioViewController can be added here
        User user = modelController.getUser();
        Portfolio portfolio = user.getPortfolio();
        availableBalanceLabel.setText("Balance: $" + portfolio.getBalance().toString());

    }

    @Override
    public void modelChanged(ModelEvent event) {

    }

    @FXML
    public void onMainView(ActionEvent event) {
        viewSwitcher.switchTo(EView.MAINVIEW);
    }

    @FXML
    private Label availableBalanceLabel;

}
