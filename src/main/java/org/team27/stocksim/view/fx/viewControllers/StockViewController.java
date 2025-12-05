package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.view.ViewAdapter;
import org.team27.stocksim.view.fx.EView;
import org.team27.stocksim.view.fx.SelectedStockService;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StockViewController extends ViewControllerBase
        implements ViewAdapter.PriceUpdateListener, ViewAdapter.TradeSettledListener {

    @FXML
    private Label favoriteIcon;

    // Nya fält kopplade till dina fx:id i FXML:
    @FXML
    private Label symbolLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLabel; // om du lade till fx:id
    @FXML
    private Label orderPriceLabel; // om du lade till fx:id

    @FXML
    private Label availableBalanceLabel;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private Label orderQuantityLabel;

    @FXML
    private Label orderTotalLabel;

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
        // Allow digits and one decimal point
        if (!text.matches("\\d*\\.?\\d*")) {
            priceField.setText(text.replaceAll("[^\\d.]", ""));
            // Remove extra decimal points (keep only the first one)
            int firstDot = priceField.getText().indexOf('.');
            if (firstDot != -1) {
                String beforeDot = priceField.getText().substring(0, firstDot + 1);
                String afterDot = priceField.getText().substring(firstDot + 1).replace(".", "");
                priceField.setText(beforeDot + afterDot);
            }
        }
        updateOrderTotal();
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

    @FXML
    private void handleBuy(ActionEvent event) {
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            String priceText = priceField.getText();
            if (priceText.isEmpty()) {
                return;
            }
            BigDecimal price = new BigDecimal(priceText);

            System.out.println("Buy button clicked for stock: " + stock.getSymbol() + " Quantity: " + quantity
                    + " Price: " + price);
            modelController.buyStock(stock.getSymbol(), quantity, price);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity or price");
        }
    }

    @FXML
    private void handleSell(ActionEvent event) {
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            String priceText = priceField.getText();
            if (priceText.isEmpty()) {
                return;
            }
            BigDecimal price = new BigDecimal(priceText);

            System.out.println("Sell button clicked for stock: " + stock.getSymbol() + " Quantity: " + quantity
                    + " Price: " + price);
            modelController.sellStock(stock.getSymbol(), quantity, price);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity or price");
        }
    }

    @FXML
    private void handleSetMarketPrice(ActionEvent event) {
        if (stock != null && priceField != null) {
            BigDecimal currentPrice = stock.getCurrentPrice();
            priceField.setText(currentPrice.toString());
            updateOrderTotal();
        }
    }

    @FXML
    private LineChart<Number, Number> priceChart;

    @FXML
    private Button btn1D;
    @FXML
    private Button btn1W;
    @FXML
    private Button btn1M;
    @FXML
    private Button btn1Y;

    @FXML
    private VBox trendingStocksContainer;

    private boolean isFavorite = false;
    private Instrument stock;
    private XYChart.Series<Number, Number> priceSeries;
    private int lastPriceHistorySize = 0;
    private Button activeTimePeriodButton = null;
    private HashMap<String, Label> trendingStockPriceLabels = new HashMap<>();

    // OOP-based chart management
    private ChartDataService chartDataService;
    private ChartTimePeriod currentTimePeriod;

    /**
     * Handler for the header "Home" button (FXML references `onExample`).
     * Navigates back to the main view.
     */

    @FXML
    private void initialize() {
        // Bind orderQuantityLabel to quantityField text
        if (quantityField != null && orderQuantityLabel != null) {
            quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
                orderQuantityLabel.setText(newValue);
            });
            orderQuantityLabel.setText(quantityField.getText());
        }

        // Bind price field changes to update order summary
        if (priceField != null) {
            priceField.textProperty().addListener((observable, oldValue, newValue) -> {
                updateOrderTotal();
            });
        }

        // Hämta vald aktie från service
        stock = SelectedStockService.getSelectedStock();
        System.out.println("Selected stock in StockViewController: " + (stock != null ? stock.getSymbol() : "null"));

        if (stock != null) {
            // Symbol
            symbolLabel.setText(stock.getSymbol());

            // Namn/beskrivning – anpassa efter din Stock-modell
            // Om ni har t.ex. getName() / getFullName():
            nameLabel.setText(stock.getName());

            // Pris – anpassa efter vad modellen har (getCurrentPrice, getLastPrice, etc.)
            BigDecimal price = stock.getCurrentPrice(); // EXEMPEL – byt till rätt getter
            if (priceLabel != null) {
                priceLabel.setText(price.toString());
            }

            // Initialize price field with current price
            if (priceField != null) {
                priceField.setText(price.toString());
            }

            if (orderPriceLabel != null) {
                orderPriceLabel.setText(price + " SEK");
            }

            // Initialize chart data service and default time period
            chartDataService = new ChartDataService();
            currentTimePeriod = ChartTimePeriod.ONE_DAY;

            // Initialize and populate the price chart
            initializePriceChart();

            // Set default active button (1D)
            if (btn1D != null) {
                setActiveTimePeriodButton(btn1D);
            }
        }
    }

    /**
     * Populates the trending stocks panel with the top 5 most valuable stocks.
     */
    private void populateTrendingStocks() {
        if (trendingStocksContainer == null || modelController == null) {
            return;
        }

        try {
            // Get all stocks and sort by price (most valuable first)
            HashMap<String, Instrument> allStocks = modelController.getAllStocks();
            if (allStocks == null || allStocks.isEmpty()) {
                return;
            }

            List<Instrument> topStocks = allStocks.values().stream()
                    .sorted((s1, s2) -> s2.getCurrentPrice().compareTo(s1.getCurrentPrice()))
                    .limit(3)
                    .collect(Collectors.toList());

            // Clear existing items
            trendingStocksContainer.getChildren().clear();
            trendingStockPriceLabels.clear();

            // Create UI for each trending stock
            for (Instrument instrument : topStocks) {
                HBox stockItem = createTrendingStockItem(instrument);
                trendingStocksContainer.getChildren().add(stockItem);
            }
        } catch (Exception e) {
            System.err.println("Error populating trending stocks: " + e.getMessage());
            e.printStackTrace();
        }

        // Initialize order total
        updateOrderTotal();
    }

    /**
     * Creates a single trending stock item UI component.
     */
    private HBox createTrendingStockItem(Instrument instrument) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.getStyleClass().add("trending-stock-item");
        container.setSpacing(8);

        // Left side: Symbol and Name in VBox
        VBox infoBox = new VBox();
        infoBox.setSpacing(1);

        Label symbolLabel = new Label(instrument.getSymbol());
        symbolLabel.getStyleClass().add("trending-symbol");

        Label nameLabel = new Label(instrument.getName());
        nameLabel.getStyleClass().add("trending-name");

        infoBox.getChildren().addAll(symbolLabel, nameLabel);

        // Spacer to push price to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Right side: Price
        Label priceLabel = new Label(String.format("%.2f", instrument.getCurrentPrice()));
        priceLabel.getStyleClass().add("trending-price");

        // Store price label reference for dynamic updates
        trendingStockPriceLabels.put(instrument.getSymbol(), priceLabel);

        container.getChildren().addAll(infoBox, spacer, priceLabel);

        // Make clickable - navigate to this stock's detail view
        container.setOnMouseClicked(event -> {
            SelectedStockService.setSelectedStock(instrument);
            viewSwitcher.switchTo(EView.STOCKVIEW);
        });

        return container;
    }

    @FXML
    private void onTimePeriodClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        setActiveTimePeriodButton(clickedButton);

        // Update current time period using enum
        String periodLabel = clickedButton.getText();
        currentTimePeriod = ChartTimePeriod.fromLabel(periodLabel);

        // Refresh chart with new time period
        refreshChartForTimePeriod();

        System.out.println("Selected time period: " + currentTimePeriod.getLabel() + " (" + currentTimePeriod.getDays()
                + " days)");
    }

    private void setActiveTimePeriodButton(Button button) {
        // Remove active class from previously active button
        if (activeTimePeriodButton != null) {
            activeTimePeriodButton.getStyleClass().remove("time-period-btn-active");
        }

        // Add active class to new button
        if (button != null && !button.getStyleClass().contains("time-period-btn-active")) {
            button.getStyleClass().add("time-period-btn-active");
        }

        activeTimePeriodButton = button;
    }

    private void initializePriceChart() {
        if (priceChart == null || stock == null || chartDataService == null) {
            return;
        }

        // Use ChartDataService to prepare initial data
        priceSeries = chartDataService.prepareChartData(
                stock.getPriceHistory(),
                currentTimePeriod);
        priceSeries.setName(stock.getSymbol() + " Price");

        lastPriceHistorySize = stock.getPriceHistory().getPoints().size();

        // Add the series to the chart
        priceChart.getData().clear();
        priceChart.getData().add(priceSeries);

        // Update time axis label based on period
        priceChart.getXAxis().setLabel(chartDataService.getTimeAxisLabel(currentTimePeriod));

        // Style the chart
        priceChart.setCreateSymbols(false); // Don't show dots on the line
        priceChart.setAnimated(false); // Disable animation for better performance
    }

    private void updateChartData() {
        if (priceSeries == null || stock == null || chartDataService == null) {
            return;
        }

        // Use ChartDataService to update chart efficiently
        lastPriceHistorySize = chartDataService.updateChartData(
                priceSeries,
                stock.getPriceHistory(),
                currentTimePeriod,
                lastPriceHistorySize);
    }

    /**
     * Refresh the entire chart when time period changes.
     * This recreates the chart data based on the new time period filter.
     */
    private void refreshChartForTimePeriod() {
        if (priceChart == null || stock == null || chartDataService == null) {
            return;
        }

        // Clear and recreate the series with new time period
        priceSeries.getData().clear();

        XYChart.Series<Number, Number> newSeries = chartDataService.prepareChartData(
                stock.getPriceHistory(),
                currentTimePeriod);

        priceSeries.getData().addAll(newSeries.getData());
        lastPriceHistorySize = stock.getPriceHistory().getPoints().size();

        // Update time axis label
        priceChart.getXAxis().setLabel(chartDataService.getTimeAxisLabel(currentTimePeriod));
    }

    /*
     * @FXML
     * public void onExample(ActionEvent event) {
     * viewSwitcher.switchTo(EView.CREATESTOCK);
     * }
     */

    @FXML
    public void onMainView(ActionEvent event) {
        viewSwitcher.switchTo(EView.MAINVIEW);
    }

    @FXML
    public void onPortfolioView(ActionEvent event) {
        viewSwitcher.switchTo(EView.PORTFOLIOVIEW);
    }

    @FXML
    private void onToggleFavorite() {
        isFavorite = !isFavorite;
        favoriteIcon.setText(isFavorite ? "★" : "☆");
    }

    @Override
    public void onPriceUpdate(HashMap<String, ? extends Instrument> stocks) {
        // Uppdatera priset om det har ändrats
        if (stock != null) {
            BigDecimal newPrice = stock.getCurrentPrice();
            Platform.runLater(() -> {
                priceLabel.setText(newPrice.toString());
                // Update price field if user hasn't entered a custom price
                if (priceField != null && priceField.getText().isEmpty()) {
                    priceField.setText(newPrice.toString());
                }
                // Update the chart with new price data
                updateChartData();
                updateOrderTotal();
            });
        }

        // Update trending stock prices dynamically
        Platform.runLater(() -> {
            for (String symbol : trendingStockPriceLabels.keySet()) {
                Instrument instrument = stocks.get(symbol);
                if (instrument != null) {
                    Label label = trendingStockPriceLabels.get(symbol);
                    label.setText(String.format("%.2f", instrument.getCurrentPrice()));
                }
            }
        });
    }

    @Override
    public void onTradeSettled() {
        User user = modelController.getUser();
        Portfolio portfolio = user.getPortfolio();
        BigDecimal balance = portfolio.getBalance();
        Platform.runLater(() -> {
            availableBalanceLabel.setText("Balance: $" + balance.toString());
        });
    }

    @Override
    protected void onInit() {
        // Register for events we care about
        viewAdapter.addPriceUpdateListener(this);
        viewAdapter.addTradeSettledListener(this);

        User user = modelController.getUser();
        Portfolio portfolio = user.getPortfolio();
        BigDecimal balance = portfolio.getBalance();
        availableBalanceLabel.setText("Balance: $" + balance.toString());

        // Populate trending stocks after modelController is initialized
        populateTrendingStocks();
    }

}
