package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.portfolio.Position;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.model.util.dto.OrderDTO;
import org.team27.stocksim.view.fx.EView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import org.team27.stocksim.view.ViewAdapter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.team27.stocksim.model.util.dto.InstrumentDTO;

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
    private ListView<PositionData> positionsListView;

    @FXML
    private ListView<String> ordersListView;

    private ObservableList<PositionData> positionsList = FXCollections.observableArrayList();
    private ObservableList<String> ordersList = FXCollections.observableArrayList();

    @Override
    protected void onInit() {
        // Register for portfolio change events
        viewAdapter.addPortfolioChangedListener(this);
        viewAdapter.addTradeSettledListener(this);
        User user = modelController.getUser();
        Portfolio portfolio = user.getPortfolio();
        BigDecimal balance = portfolio.getBalance();
        availableBalanceLabel.setText("Balance: $" + balance.toString());

        // Set up ListViews
        if (positionsListView != null) {
            positionsListView.setItems(positionsList);
            positionsListView.setFixedCellSize(48); // Set fixed cell height for better rendering
            // Custom cell factory to display data in columns
            positionsListView.setCellFactory(lv -> new ListCell<PositionData>() {
                @Override
                protected void updateItem(PositionData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        HBox hbox = new HBox();
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        
                        // Stock - 152px
                        Label stockLabel = new Label(item.symbol);
                        stockLabel.setPrefWidth(152);
                        stockLabel.setAlignment(Pos.CENTER);
                        
                        // Quantity - 155px
                        Label qtyLabel = new Label(String.valueOf(item.quantity));
                        qtyLabel.setPrefWidth(155);
                        qtyLabel.setAlignment(Pos.CENTER);
                        
                        // Current Price - 110px
                        Label priceLabel = new Label(String.format("$%.2f", item.currentPrice));
                        priceLabel.setPrefWidth(110);
                        priceLabel.setAlignment(Pos.CENTER);
                        
                        // Gain/Loss - 194px
                        String sign = item.gainLoss.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                        Label gainLabel = new Label(sign + String.format("$%.2f", item.gainLoss));
                        gainLabel.setPrefWidth(194);
                        gainLabel.setAlignment(Pos.CENTER);
                        
                        // Color coding
                        if (item.gainLoss.compareTo(BigDecimal.ZERO) > 0) {
                            gainLabel.getStyleClass().add("text-positive");
                        } else if (item.gainLoss.compareTo(BigDecimal.ZERO) < 0) {
                            gainLabel.getStyleClass().add("text-negative");
                        }
                        
                        hbox.getChildren().addAll(stockLabel, qtyLabel, priceLabel, gainLabel);
                        setGraphic(hbox);
                        setText(null);
                    }
                }
            });
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
    private void updateTotalValueAndGainLoss(Portfolio portfolio) {
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
            totalGainLossLabel.getStyleClass().removeAll("text-positive", "text-negative");
            if (totalGainLoss.compareTo(BigDecimal.ZERO) > 0) {
                totalGainLossLabel.getStyleClass().add("text-positive");
            } else if (totalGainLoss.compareTo(BigDecimal.ZERO) < 0) {
                totalGainLossLabel.getStyleClass().add("text-negative");
            }
        }

        // Update gain/loss percentage label
        if (totalGainLossPercentLabel != null) {
            String sign = gainLossPercent.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            totalGainLossPercentLabel.setText(sign + String.format("%.2f", gainLossPercent) + "%");

            // Apply styling based on profit/loss
            totalGainLossPercentLabel.getStyleClass().removeAll("text-positive", "text-negative");
            if (gainLossPercent.compareTo(BigDecimal.ZERO) > 0) {
                totalGainLossPercentLabel.getStyleClass().add("text-positive");
            } else if (gainLossPercent.compareTo(BigDecimal.ZERO) < 0) {
                totalGainLossPercentLabel.getStyleClass().add("text-negative");
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
    private void updatePositionsDisplay(Portfolio portfolio) {
        Map<String, Position> positions = portfolio.getPositions();
        Map<String, BigDecimal> currentPrices = getCurrentPrices();

        positionsList.clear();

        if (!positions.isEmpty()) {
            positions.forEach((symbol, position) -> {
                int quantity = position.getQuantity();
                BigDecimal avgCost = position.getAverageCost();
                BigDecimal totalCost = avgCost.multiply(BigDecimal.valueOf(quantity));
                BigDecimal currentPrice = currentPrices.getOrDefault(symbol, BigDecimal.ZERO);
                BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(quantity));
                BigDecimal gainLoss = currentValue.subtract(totalCost);

                positionsList.add(new PositionData(symbol, quantity, currentPrice, gainLoss));
            });
        }
        
        // Force ListView to refresh its layout
        if (positionsListView != null) {
            positionsListView.refresh();
        }
    }

    /**
     * Updates the active orders display
     */
    private void updateOrdersDisplay(User user) {
        List<OrderDTO> activeOrders = user.getOrderHistory().getActiveOrdersDTO();

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

    /**
     * Data class to hold position information for display
     */
    private static class PositionData {
        final String symbol;
        final int quantity;
        final BigDecimal currentPrice;
        final BigDecimal gainLoss;

        PositionData(String symbol, int quantity, BigDecimal currentPrice, BigDecimal gainLoss) {
            this.symbol = symbol;
            this.quantity = quantity;
            this.currentPrice = currentPrice;
            this.gainLoss = gainLoss;
        }
    }

}
