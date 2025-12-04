package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.observer.ModelEvent;
import org.team27.stocksim.view.fx.EView;
import org.team27.stocksim.view.fx.SelectedStockService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.instruments.Stock;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.User;

import java.math.BigDecimal;
import java.util.HashMap;

public class MainViewController extends ViewControllerBase {

    @Override
    protected void onInit() {
        modelController.addObserver(this);
        stockListView.setItems(stockList);

        // Set a custom cell factory to display stock items
        stockListView.setCellFactory(listView -> new StockListCell());

        // Get all stocks from the model
        HashMap<String, Instrument> stocks = modelController.getAllStocks();
        stockList.addAll(stocks.values());

        User user = modelController.getUser();
        Portfolio portfolio = user.getPortfolio();
        BigDecimal balance = portfolio.getBalance();
        availableBalanceLabel.setText("Balance: $" + balance.toString());
    }

    @FXML
    private ListView<Instrument> stockListView;

    private ObservableList<Instrument> stockList = FXCollections.observableArrayList();

    @FXML
    private Label availableBalanceLabel;

    /*
     * @FXML
     * public void onExample(ActionEvent event) {
     * viewSwitcher.switchTo(EView.CREATESTOCK);
     * }
     */

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

    @FXML
    public void onPortfolioView(ActionEvent event) {
        viewSwitcher.switchTo(EView.PORTFOLIOVIEW);
    }

    @Override
    public void modelChanged(ModelEvent event) {
        switch (event.getType()) {
            case PRICE_UPDATE -> updatePrice(event);
        }
    }

    private void updatePrice(ModelEvent event) {
        HashMap<String, Stock> stocks = (HashMap<String, Stock>) event.getPayload();

        Platform.runLater(() -> {
            // Update the existing list items to trigger UI refresh
            for (int i = 0; i < stockList.size(); i++) {
                Instrument existing = stockList.get(i);
                Stock updated = stocks.get(existing.getSymbol());
                if (updated != null) {
                    stockList.set(i, updated);
                }
            }
        });
    }

    // ListCell object for displaying Stock items
    class StockListCell extends ListCell<Instrument> {
        private final HBox content;
        private final Label symbol;
        private final Label meta;
        private final Label price;
        private final Button actionButton;

        public StockListCell() {
            symbol = new Label();
            symbol.getStyleClass().add("stock-symbol");
            meta = new Label();
            meta.getStyleClass().add("stock-meta");
            price = new Label();
            price.getStyleClass().add("stock-price");

            VBox stockInfo = new VBox(symbol, meta);
            stockInfo.getStyleClass().add("stock-info");

            // Create a spacer region to push the price and button to the right
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Trade button
            actionButton = new Button("Trade");
            actionButton.getStyleClass().add("btn-highlighted");
            actionButton.setOnAction(event -> {
                Instrument instrument = getItem();
                if (instrument != null) {
                    handleButtonClick(instrument);
                }
            });

            content = new HBox(stockInfo, spacer, price, actionButton);
            content.setAlignment(Pos.CENTER_LEFT);
            content.getStyleClass().add("content");
        }

        @Override
        protected void updateItem(Instrument stock, boolean empty) {
            super.updateItem(stock, empty);
            if (empty || stock == null) {
                setText(null);
                setGraphic(null);
            } else {
                symbol.setText(stock.getSymbol());
                meta.setText("Technology");
                price.setText(String.format("$%.2f", stock.getCurrentPrice()));
                setGraphic(content);
            }
        }

        // Trade button handler
        private void handleButtonClick(Instrument instrument) {
            System.out.println(instrument.getSymbol());

            // 1. Spara vald aktie
            SelectedStockService.setSelectedStock(instrument);
            // 2. Byt till stock view
            viewSwitcher.switchTo(EView.STOCKVIEW);
        }

    }
}
