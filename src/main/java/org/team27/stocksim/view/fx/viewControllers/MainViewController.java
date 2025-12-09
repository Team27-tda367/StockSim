package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.view.ViewAdapter;
import org.team27.stocksim.view.fx.EView;
import org.team27.stocksim.view.fx.SelectedStockService;

import java.util.ArrayList;
import java.util.HashMap;

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

import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class MainViewController extends ViewControllerBase 
        implements ViewAdapter.PriceUpdateListener {

    @Override
    protected void onInit() {
        initCategories(modelController.getAllCategories());

        viewAdapter.addPriceUpdateListener(this);
        stockListView.setItems(stockList);

        // Set a custom cell factory to display stock items
        stockListView.setCellFactory(listView -> new StockListCell());

        // Get all stocks from the model
        HashMap<String, Instrument> stocks = modelController.getStocks("All");
        stockList.addAll(stocks.values());

        User user = modelController.getUser();
        Portfolio portfolio = user.getPortfolio();
        BigDecimal balance = portfolio.getBalance();
        availableBalanceLabel.setText("Balance: $" + String.format("%.2f", balance));

    }

    @FXML private HBox categoryBox;
    @FXML private Button btnAll;
    @FXML private Region spacer;

    private final ArrayList<Button> categoryButtons = new ArrayList<>();


    /** Category filtering */
    private void initCategories(ArrayList<String> categories) {

        // Add "All" button to highlight-selector
        categoryButtons.add(btnAll);

        // Insert enum buttons right after the "All" button
        int insertIndex = categoryBox.getChildren().indexOf(btnAll) + 1;

        for (String c : categories) {
            Button b = new Button(c);
            b.getStyleClass().add("btn-secondary");
            categoryBox.getChildren().add(insertIndex, b);

            categoryButtons.add(b);
            insertIndex++; // Move the insertion point

            // Add click handler
            b.setOnAction(event -> categoryClicked(b));
        }

        // Add click handler for the All button too
        btnAll.setOnAction(event -> categoryClicked(btnAll));
    }

    private void categoryClicked(Button selected) {
        highlight(selected);
        fetchStocksFromCategory(selected.getText());
    }

    private void fetchStocksFromCategory(String category) {
        stockList.clear();
        HashMap<String, Instrument> stocks = modelController.getStocks(category);
        stockList.addAll(stocks.values());
    }

    /* Highlights the selected button and resets others. */
    private void highlight(Button selected) {
        for (Button b : categoryButtons) {
            b.getStyleClass().removeAll("btn-secondary", "btn-highlighted");

            if (b == selected) {
                b.getStyleClass().add("btn-highlighted");
            } else {
                b.getStyleClass().add("btn-secondary");
            }
        }
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
    public void onPriceUpdate(HashMap<String, ? extends Instrument> stocks) {

        Platform.runLater(() -> {
            // Update the existing list items to trigger UI refresh
            for (int i = 0; i < stockList.size(); i++) {
                Instrument existing = stockList.get(i);
                Instrument updated = stocks.get(existing.getSymbol());
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
                meta.setText(stock.getCategory());
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
