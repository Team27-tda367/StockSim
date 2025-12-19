package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.view.fx.EView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import org.team27.stocksim.dto.*;
import org.team27.stocksim.view.ViewAdapter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortfolioViewController extends ViewControllerBase
        implements ViewAdapter.PortfolioChangedListener, ViewAdapter.TradeSettledListener {

    @FXML
    private Label availableBalanceLabel;

    @FXML
    private Label totalValueLabel;

    @FXML
    private Label totalGainLossLabel;

    @FXML
    private Label totalGainLossPercentLabel;

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
        UserDTO user = modelController.getUser();
        PortfolioDTO portfolio = user.getPortfolio();
        BigDecimal balance = portfolio.getBalance();
        availableBalanceLabel.setText("Balance: $" + balance.toString());

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
        UserDTO user = modelController.getUser();
        PortfolioDTO portfolio = user.getPortfolio();

        // Update balance
        BigDecimal balance = portfolio.getBalance();
        availableBalanceLabel.setText("Balance: $" + String.format("%.2f", balance));

        // Update total value and gain/loss
        updateTotalValueAndGainLoss(portfolio);

        // Update positions
        updatePositionsDisplay(portfolio);

        // Update active orders
        updateOrdersDisplay(user);
    }

    /**
     * Updates the total value and gain/loss labels based on current portfolio
     * positions
     */
    private void updateTotalValueAndGainLoss(PortfolioDTO portfolio) {
        // Get current stock prices from model
        Map<String, BigDecimal> currentPrices = getCurrentPrices();

        // Use Portfolio model methods to calculate values
        BigDecimal totalValue = portfolio.getTotalValue(currentPrices);
        BigDecimal totalGainLoss = portfolio.getTotalGainLoss(currentPrices);
        BigDecimal gainLossPercent = portfolio.getGainLossPercentage(currentPrices);

        // Update total value label
        if (totalValueLabel != null) {
            totalValueLabel.setText("$" + String.format("%.2f", totalValue));
        }

        // Update gain/loss amount label
        if (totalGainLossLabel != null) {
            String sign = totalGainLoss.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            totalGainLossLabel.setText(sign + "$" + String.format("%.2f", totalGainLoss));

            // Apply styling based on profit/loss
            totalGainLossLabel.getStyleClass().removeAll("text-success", "text-danger");
            if (totalGainLoss.compareTo(BigDecimal.ZERO) > 0) {
                totalGainLossLabel.getStyleClass().add("text-success");
            } else if (totalGainLoss.compareTo(BigDecimal.ZERO) < 0) {
                totalGainLossLabel.getStyleClass().add("text-danger");
            }
        }

        // Update gain/loss percentage label
        if (totalGainLossPercentLabel != null) {
            String sign = gainLossPercent.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            totalGainLossPercentLabel.setText(sign + String.format("%.2f", gainLossPercent) + "%");

            // Apply styling based on profit/loss
            totalGainLossPercentLabel.getStyleClass().removeAll("text-success", "text-danger");
            if (gainLossPercent.compareTo(BigDecimal.ZERO) > 0) {
                totalGainLossPercentLabel.getStyleClass().add("text-success");
            } else if (gainLossPercent.compareTo(BigDecimal.ZERO) < 0) {
                totalGainLossPercentLabel.getStyleClass().add("text-danger");
            }
        }
    }

    /**
     * Gets current prices for all stocks from the model.
     *
     * @return map of symbol to current price
     */
    private Map<String, BigDecimal> getCurrentPrices() {
        Map<String, BigDecimal> prices = new HashMap<>();

        // Fetch all stocks from all categories
        for (String category : modelController.getAllCategories()) {
            HashMap<String, InstrumentDTO> stocks = modelController.getStocks(category);
            stocks.forEach((symbol, stock) -> prices.put(symbol, stock.getPrice()));
        }

        return prices;
    }

    /**
     * Updates the positions display with detailed information
     */
    private void updatePositionsDisplay(PortfolioDTO portfolio) {
        Map<String, PositionDTO> positions = portfolio.getPositions();

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
    private void updateOrdersDisplay(UserDTO user) {
        List<OrderDTO> activeOrders = user.getOrderHistory().getActiveOrders();

        ordersList.clear();

        if (activeOrders.isEmpty()) {
            ordersList.add("No active orders");
        } else {
            activeOrders.forEach(order -> {
                String orderStr = String.format("%s %s: %d @ $%.2f [%s]",
                        order.getSide(),
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
