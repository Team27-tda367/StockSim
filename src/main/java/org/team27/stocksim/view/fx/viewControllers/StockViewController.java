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

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.User;

import java.math.BigDecimal;
import java.util.HashMap;

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
    private Label orderQuantityLabel;

    @FXML
    private void validateNumberInput() {
        String text = quantityField.getText();
        if (!text.matches("\\d*")) {
            quantityField.setText(text.replaceAll("[^\\d]", ""));
        }
    }

    @FXML
    private void handleBuy(ActionEvent event) {
        // Hantera köp-logik här
        int quantity = Integer.parseInt(quantityField.getText()); // Hämta kvantitet från fältet
        System.out.println("Buy button clicked for stock: " + stock.getSymbol() + " Quantity: " + quantity);
        modelController.buyStock(stock.getSymbol(), quantity, stock.getCurrentPrice());
    }

    @FXML
    private void handleSell(ActionEvent event) {
        // Hantera sälj-logik här
        System.out.println("Sell button clicked for stock: " + stock.getSymbol());
        // Du kan lägga till sälj-logik som att öppna en sälj-dialog eller direkt
        // genomföra försäljningen
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

    private boolean isFavorite = false;
    private Instrument stock;
    private XYChart.Series<Number, Number> priceSeries;
    private int lastPriceHistorySize = 0;
    private Button activeTimePeriodButton = null;

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
            if (orderPriceLabel != null) {
                orderPriceLabel.setText(price + " SEK");
            }

            // Initialize chart data service and default time period
            chartDataService = new ChartDataService();
            currentTimePeriod = ChartTimePeriod.ONE_DAY;

            // Initialize and populate the price chart
            initializePriceChart();
        }

        // Set default active button (1D)
        if (btn1D != null) {
            setActiveTimePeriodButton(btn1D);
        }
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
                orderPriceLabel.setText(newPrice + " SEK");
                // Update the chart with new price data
                updateChartData();
            });
        }
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
    }

}
