package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.model.util.dto.*;
import org.team27.stocksim.view.ViewAdapter;
import org.team27.stocksim.view.fx.EView;
import org.team27.stocksim.view.fx.chart.ChartDataService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;

import org.team27.stocksim.model.util.dto.InstrumentDTO;
import org.team27.stocksim.view.ViewAdapter;
import org.team27.stocksim.view.fx.EView;
import org.team27.stocksim.view.fx.chart.ChartDataService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class StockViewController extends ViewControllerBase
        implements ViewAdapter.PriceUpdateListener, ViewAdapter.TradeSettledListener,
        ViewAdapter.PortfolioChangedListener {

    // Stock Information Labels
    @FXML
    private Label symbolLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label availableBalanceLabel;

    // Order Input Fields
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;

    // Order Summary Labels
    @FXML
    private Label orderQuantityLabel;
    @FXML
    private Label orderPriceLabel;
    @FXML
    private Label orderTotalLabel;

    // Price Chart Components
    @FXML
    private LineChart<Number, Number> priceChart;

    // Positions and Orders ListViews
    @FXML
    private ListView<String> positionsListView;
    @FXML
    private ListView<String> ordersListView;

    // Observable lists for positions and orders
    private ObservableList<String> positionsList = FXCollections.observableArrayList();
    private ObservableList<String> ordersList = FXCollections.observableArrayList();

    // Model Data
    private InstrumentDTO stock;
    private XYChart.Series<Number, Number> priceSeries;
    private int lastPriceHistorySize = 0;
    private ChartDataService chartDataService;

    // ==================== Initialization ====================

    @FXML
    private void initialize() {
        bindQuantityFieldToLabel();
        bindPriceFieldToOrderUpdate();
    }

    @Override
    protected void onInit() {
        registerEventListeners();
        updateBalanceDisplay();
        loadAndDisplayStock();
        initializeChart();
        initializeListViews();
        updateStockPositionAndOrders();
    }

    private void registerEventListeners() {
        viewAdapter.addPriceUpdateListener(this);
        viewAdapter.addTradeSettledListener(this);
        viewAdapter.addPortfolioChangedListener(this);
    }

    private void updateBalanceDisplay() {
        PortfolioDTO portfolio = modelController.getUser().getPortfolio();
        availableBalanceLabel.setText("Balance: $" + portfolio.getBalance().toString());
    }

    private void loadAndDisplayStock() {
        stock = modelController.getSelectedStock();
        if (stock != null) {
            displayStockInformation();
            initializePriceFields();
        }
    }

    private void displayStockInformation() {
        symbolLabel.setText(stock.getSymbol());
        nameLabel.setText(stock.getName());
        categoryLabel.setText(stock.getCategory());
        priceLabel.setText(stock.getPrice().toString());
    }

    private void initializePriceFields() {
        BigDecimal price = stock.getPrice();
        priceField.setText(price.toString());
        orderPriceLabel.setText(price + " SEK");
    }

    private void initializeChart() {
        if (stock != null) {
            chartDataService = new ChartDataService();
            initializePriceChart();
        }
    }

    private void bindQuantityFieldToLabel() {
        if (quantityField != null && orderQuantityLabel != null) {
            quantityField.textProperty()
                    .addListener((observable, oldValue, newValue) -> orderQuantityLabel.setText(newValue));
            orderQuantityLabel.setText(quantityField.getText());
        }
    }

    private void bindPriceFieldToOrderUpdate() {
        if (priceField != null) {
            priceField.textProperty().addListener((observable, oldValue, newValue) -> updateOrderTotal());
        }
    }

    private void initializeListViews() {
        if (positionsListView != null) {
            positionsListView.setItems(positionsList);
        }
        if (ordersListView != null) {
            ordersListView.setItems(ordersList);
        }
    }

    // ==================== Input Validation ====================

    @FXML
    private void validateNumberInput() {
        String text = quantityField.getText();
        if (!text.matches("\\d*")) {
            quantityField.setText(text.replaceAll("[^\\d]", ""));
        }
        updateOrderTotal();
    }

    @FXML
    private void validatePriceInput() {
        String text = priceField.getText();
        if (!text.matches("\\d*\\.?\\d*")) {
            priceField.setText(text.replaceAll("[^\\d.]", ""));
            removeExtraDecimalPoints();
        }
        updateOrderTotal();
    }

    private void removeExtraDecimalPoints() {
        int firstDot = priceField.getText().indexOf('.');
        if (firstDot != -1) {
            String beforeDot = priceField.getText().substring(0, firstDot + 1);
            String afterDot = priceField.getText().substring(firstDot + 1).replace(".", "");
            priceField.setText(beforeDot + afterDot);
        }
    }

    private void updateOrderTotal() {
        try {
            String quantityText = quantityField.getText();
            String priceText = priceField.getText();

            if (!quantityText.isEmpty() && !priceText.isEmpty()) {
                int quantity = Integer.parseInt(quantityText);
                BigDecimal price = new BigDecimal(priceText);
                BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));

                orderPriceLabel.setText(price + " SEK");
                orderTotalLabel.setText(String.format("%.2f SEK", total));
            }
        } catch (NumberFormatException e) {
            // Ignore invalid input
        }
    }

    // ==================== Order Actions ====================

    @FXML
    private void handleBuy(ActionEvent event) {
        executeLimitOrder(true);
    }

    @FXML
    private void handleSell(ActionEvent event) {
        executeLimitOrder(false);
    }

    @FXML
    private void handleBuyMarketOrder(ActionEvent event) {
        executeMarketOrder(true);
    }

    @FXML
    private void handleSellMarketOrder(ActionEvent event) {
        executeMarketOrder(false);
    }

    @FXML
    private void handleSetMarketPrice(ActionEvent event) {
        if (stock != null && priceField != null) {
            priceField.setText(stock.getPrice().toString());
            updateOrderTotal();
        }
    }

    private void executeLimitOrder(boolean isBuy) {
        try {
            int quantity = getQuantity();
            BigDecimal price = getPrice();

            if (isBuy) {
                modelController.buyStock(stock.getSymbol(), quantity, price);
            } else {
                modelController.sellStock(stock.getSymbol(), quantity, price);
            }
        } catch (NumberFormatException e) {
            // Invalid input, do nothing
        }
    }

    private void executeMarketOrder(boolean isBuy) {
        try {
            int quantity = getQuantity();

            if (isBuy) {
                modelController.placeMarketBuyOrder(stock.getSymbol(), quantity);
            } else {
                modelController.placeMarketSellOrder(stock.getSymbol(), quantity);
            }
        } catch (NumberFormatException e) {
        }
    }

    private int getQuantity() throws NumberFormatException {
        return Integer.parseInt(quantityField.getText());
    }

    private BigDecimal getPrice() throws NumberFormatException {
        String priceText = priceField.getText();
        if (priceText.isEmpty()) {
            throw new NumberFormatException("Price cannot be empty");
        }
        return new BigDecimal(priceText);
    }

    // ==================== Chart Management ====================

    private void initializePriceChart() {
        if (priceChart == null || stock == null || chartDataService == null) {
            return;
        }

        priceSeries = chartDataService.prepareChartData(stock.getPriceHistory());
        priceSeries.setName(stock.getSymbol() + " Price");
        lastPriceHistorySize = stock.getPriceHistory().getPoints().size();

        priceChart.getData().clear();
        priceChart.getData().add(priceSeries);
        priceChart.getXAxis().setLabel("Time");
        priceChart.setCreateSymbols(false);
        priceChart.setAnimated(false);
    }

    private void updateChartData() {
        if (priceSeries == null || stock == null || chartDataService == null) {
            return;
        }

        lastPriceHistorySize = chartDataService.updateChartData(
                priceSeries,
                stock.getPriceHistory(),
                lastPriceHistorySize);
    }

    // ==================== Navigation ====================

    @FXML
    public void onMainView(ActionEvent event) {
        viewSwitcher.switchTo(EView.MAINVIEW);
    }

    @FXML
    public void onPortfolioView(ActionEvent event) {
        viewSwitcher.switchTo(EView.PORTFOLIOVIEW);
    }

    // ==================== Event Listeners ====================

    @Override
    public void onPriceUpdate(HashMap<String, ? extends InstrumentDTO> stocks) {
        if (stock == null) {
            return;
        }

        InstrumentDTO updatedStock = stocks.get(stock.getSymbol());
        if (updatedStock != null) {
            stock = updatedStock;
            Platform.runLater(() -> updatePriceDisplay(stock.getPrice()));
        }
    }

    private void updatePriceDisplay(BigDecimal newPrice) {
        priceLabel.setText(newPrice.toString());
        orderPriceLabel.setText(newPrice + " SEK");

        if (priceField != null && priceField.getText().isEmpty()) {
            priceField.setText(newPrice.toString());
        }

        updateChartData();
        updateOrderTotal();
    }

    @Override
    public void onTradeSettled() {
        Platform.runLater(() -> {
            updateBalanceDisplay();
            updateStockPositionAndOrders();
        });
    }

    @Override
    public void onPortfolioChanged() {
        Platform.runLater(this::updateStockPositionAndOrders);
    }

    // ==================== Positions and Orders Display ====================

    /**
     * Updates the positions and orders display for the selected stock
     */
    private void updateStockPositionAndOrders() {
        if (stock == null) {
            return;
        }

        UserDTO user = modelController.getUser();
        PortfolioDTO portfolio = user.getPortfolio();

        // Update position for this stock
        updatePositionDisplay(portfolio);

        // Update active orders for this stock
        updateOrdersDisplay(user);
    }

    /**
     * Updates the position display for the selected stock
     */
    private void updatePositionDisplay(PortfolioDTO portfolio) {
        positionsList.clear();

        PositionDTO position = portfolio.getPosition(stock.getSymbol());

        if (position == null || position.getQuantity() == 0) {
            positionsList.add("No position in " + stock.getSymbol());
        } else {
            int quantity = position.getQuantity();
            BigDecimal avgCost = position.getAverageCost();
            BigDecimal totalCost = avgCost.multiply(BigDecimal.valueOf(quantity));

            String positionStr = String.format("%s: %d shares @ $%.2f avg (Total: $%.2f)",
                    stock.getSymbol(), quantity, avgCost, totalCost);
            positionsList.add(positionStr);
        }
    }

    /**
     * Updates the active orders display for the selected stock
     */
    private void updateOrdersDisplay(UserDTO user) {
        ordersList.clear();

        List<OrderDTO> activeOrders = user.getOrderHistory().getActiveOrdersDTO();

        // Filter orders for the selected stock
        List<OrderDTO> stockOrders = activeOrders.stream()
                .filter(order -> order.getSymbol().equals(stock.getSymbol()))
                .toList();

        if (stockOrders.isEmpty()) {
            ordersList.add("No active orders for " + stock.getSymbol());
        } else {
            stockOrders.forEach(order -> {
                String orderStr = String.format("%s: %d @ $%.2f [%s]",
                        order.getSide(),
                        order.getRemainingQuantity(),
                        order.getPrice(),
                        order.getStatus());
                ordersList.add(orderStr);
            });
        }
    }
}
