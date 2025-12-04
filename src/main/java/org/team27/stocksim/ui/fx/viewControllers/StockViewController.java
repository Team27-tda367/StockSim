package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.observer.ModelEvent;
import org.team27.stocksim.ui.fx.EView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import org.team27.stocksim.model.market.Instrument;
import org.team27.stocksim.model.market.PriceHistory;
import org.team27.stocksim.model.market.PricePoint;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.ui.fx.SelectedStockService;

import java.math.BigDecimal;
import java.util.List;

public class StockViewController extends ViewControllerBase {

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
    private void handleBuy(ActionEvent event) {
        // Hantera köp-logik här
        int quantity = 1; // Exempel: köp 1 aktie - annars hämta från fält
        modelController.buyStock(stock.getSymbol(), quantity);
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

    private boolean isFavorite = false;
    private Instrument stock;
    private XYChart.Series<Number, Number> priceSeries;
    private int lastPriceHistorySize = 0;

    /**
     * Handler for the header "Home" button (FXML references `onExample`).
     * Navigates back to the main view.
     */

    @FXML
    private void initialize() {
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

            // Initialize and populate the price chart
            initializePriceChart();
        }
    }

    private void initializePriceChart() {
        if (priceChart == null || stock == null) {
            return;
        }

        // Create a new data series for the price history
        priceSeries = new XYChart.Series<>();
        priceSeries.setName(stock.getSymbol() + " Price");

        // Reset the counter and populate initial data
        lastPriceHistorySize = 0;
        PriceHistory history = stock.getPriceHistory();
        List<PricePoint> points = history.getPoints();

        // Add all existing price points
        for (int i = 0; i < points.size(); i++) {
            PricePoint point = points.get(i);
            priceSeries.getData().add(new XYChart.Data<>(i, point.getPrice().doubleValue()));
        }
        lastPriceHistorySize = points.size();

        // Add the series to the chart
        priceChart.getData().clear();
        priceChart.getData().add(priceSeries);

        // Style the chart
        priceChart.setCreateSymbols(false); // Don't show dots on the line
        priceChart.setAnimated(false); // Disable animation for better performance
    }

    private void updateChartData() {
        if (priceSeries == null || stock == null) {
            return;
        }

        PriceHistory history = stock.getPriceHistory();
        List<PricePoint> points = history.getPoints();

        int currentSize = points.size();

        // Only add new points that weren't there before
        if (currentSize > lastPriceHistorySize) {
            for (int i = lastPriceHistorySize; i < currentSize; i++) {
                PricePoint point = points.get(i);
                priceSeries.getData().add(new XYChart.Data<>(i, point.getPrice().doubleValue()));
            }
            lastPriceHistorySize = currentSize;
        } else if (currentSize < lastPriceHistorySize) {
            // Price history was reset, redraw everything
            priceSeries.getData().clear();
            for (int i = 0; i < currentSize; i++) {
                PricePoint point = points.get(i);
                priceSeries.getData().add(new XYChart.Data<>(i, point.getPrice().doubleValue()));
            }
            lastPriceHistorySize = currentSize;
        }
        // If currentSize == lastPriceHistorySize, no new data to add
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
    private void onToggleFavorite() {
        isFavorite = !isFavorite;
        favoriteIcon.setText(isFavorite ? "★" : "☆");
    }

    @Override
    public void modelChanged(ModelEvent event) {
        // Hantera modelländringar här vid behov
        switch (event.getType()) {
            case PRICE_UPDATE -> {
                // Uppdatera priset om det har ändrats
                if (stock != null) {
                    BigDecimal newPrice = stock.getCurrentPrice();
                    PriceHistory history = stock.getPriceHistory();
                    Platform.runLater(() -> {
                        priceLabel.setText(newPrice.toString());
                        orderPriceLabel.setText(newPrice + " SEK");
                        // Update the chart with new price data
                        updateChartData();
                    });
                }
            }

        }

    }

    @Override
    protected void onInit() {
        modelController.addObserver(this);

        User user = modelController.getUser();
        Portfolio portfolio = user.getPortfolio();
        BigDecimal balance = portfolio.getBalance();
        availableBalanceLabel.setText("Balance: $" + balance.toString());
    }

}
