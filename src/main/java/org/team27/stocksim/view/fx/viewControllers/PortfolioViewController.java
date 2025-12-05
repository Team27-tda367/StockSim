package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.portfolio.Position;
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
import java.util.List;
import java.util.Map;

public class PortfolioViewController extends ViewControllerBase 
        implements ViewAdapter.PortfolioChangedListener, ViewAdapter.TradeSettledListener {

    @FXML
    private Label availableBalanceLabel;

    @FXML
    private ListView<String> positionsListView;

    @FXML
    private ListView<String> ordersListView;

    private ObservableList<String> positionsList = FXCollections.observableArrayList();
    private ObservableList<String> ordersList = FXCollections.observableArrayList();

    @Override
    protected void onInit() {
        // Register for portfolio change events
        viewAdapter.addPortfolioChangedListener(this);
        viewAdapter.addTradeSettledListener(this);

        // Set up ListViews
        if (positionsListView != null) {
            positionsListView.setItems(positionsList);
        }
        if (ordersListView != null) {
            ordersListView.setItems(ordersList);
        }
    }

    @Override
    protected void onShow() {
        // Refresh the display every time the view is shown
        updatePortfolioDisplay();
    }

    @Override
    public void onPortfolioChanged() {
        // Update UI on JavaFX thread when portfolio changes
        Platform.runLater(this::updatePortfolioDisplay);
    }

    @Override
    public void onTradeSettled() {
        // Update UI on JavaFX thread when trades settle (which may affect orders)
        Platform.runLater(this::updatePortfolioDisplay);
    }

    /**
     * Updates the portfolio display with current balance, positions, and active
     * orders
     */
    private void updatePortfolioDisplay() {
        User user = modelController.getUser();
        Portfolio portfolio = user.getPortfolio();

        // Update balance
        BigDecimal balance = portfolio.getBalance();
        availableBalanceLabel.setText("Balance: $" + String.format("%.2f", balance));

        // Update positions
        updatePositionsDisplay(portfolio);

        // Update active orders
        updateOrdersDisplay(user);
    }

    /**
     * Updates the positions display with detailed information
     */
    private void updatePositionsDisplay(Portfolio portfolio) {
        Map<String, Position> positions = portfolio.getPositions();

        positionsList.clear();

        if (positions.isEmpty()) {
            positionsList.add("No positions");
        } else {
            positions.forEach((symbol, position) -> {
                int quantity = position.getQuantity();
                BigDecimal avgCost = position.getAverageCost();
                BigDecimal totalCost = avgCost.multiply(BigDecimal.valueOf(quantity));

                String positionStr = String.format("%s: %d shares @ $%.2f avg (Total: $%.2f)",
                        symbol, quantity, avgCost, totalCost);
                positionsList.add(positionStr);
            });
        }
    }

    /**
     * Updates the active orders display
     */
    private void updateOrdersDisplay(User user) {
        List<Order> activeOrders = user.getOrderHistory().getActiveOrders();

        ordersList.clear();

        if (activeOrders.isEmpty()) {
            ordersList.add("No active orders");
        } else {
            activeOrders.forEach(order -> {
                String side = order.getSide() == Order.Side.BUY ? "BUY" : "SELL";
                String orderStr = String.format("%s %s: %d @ $%.2f [%s]",
                        side,
                        order.getSymbol(),
                        order.getRemainingQuantity(),
                        order.getPrice(),
                        order.getStatus());
                ordersList.add(orderStr);
            });
        }
    }

    @FXML
    public void onMainView(ActionEvent event) {
        viewSwitcher.switchTo(EView.MAINVIEW);
    }

}
