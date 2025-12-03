package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.observer.ModelEvent;
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
import org.team27.stocksim.ui.fx.SelectedStockService;

import org.team27.stocksim.model.market.Stock;
import org.team27.stocksim.ui.fx.EView;

import java.math.BigDecimal;

public class MainViewController extends ViewControllerBase {

    @Override
    protected void onInit() {
        modelController.addObserver(this);
    }

    @FXML
    private ListView<Stock> stockListView;

    private ObservableList<Stock> stockList;

    @FXML
    private void initialize() {
        // Initialize the stock list
        stockList = FXCollections.observableArrayList();
        stockListView.setItems(stockList);

        // Set a custom cell factory to display stock items
        stockListView.setCellFactory(listView -> new StockListCell());

        // Example stocks
        stockList.add(new Stock("AAPL", "Apple Inc.", BigDecimal.valueOf(0.01), 100));
        stockList.add(new Stock("GOOGL", "Alphabet Inc.", BigDecimal.valueOf(0.01), 100));
        stockList.add(new Stock("MSFT", "Microsoft Corp.", BigDecimal.valueOf(0.01), 100));

    }

    @FXML
    public void onExample(ActionEvent event) {
        viewSwitcher.switchTo(EView.CREATESTOCK);
    }

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

    @Override
    public void modelChanged(ModelEvent event) {
        System.out.println("Model changed in MainViewController");
    }

    // ListCell object for displaying Stock items
    class StockListCell extends ListCell<Stock> {
        private final HBox content;
        private final Label symbol;
        private final Label meta;
        private final Button actionButton;

        public StockListCell() {
            symbol = new Label();
            symbol.getStyleClass().add("stock-symbol");
            meta = new Label();
            meta.getStyleClass().add("stock-meta");

            VBox stockInfo = new VBox(symbol, meta);
            stockInfo.getStyleClass().add("stock-info");

            // Create a spacer region to push the button to the right
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Trade button
            actionButton = new Button("Trade");
            actionButton.getStyleClass().add("btn-highlighted");
            actionButton.setOnAction(event -> {
                Stock stock = getItem();
                if (stock != null) {
                    handleButtonClick(stock);
                }
            });

            content = new HBox(stockInfo, spacer, actionButton);
            content.setAlignment(Pos.CENTER_LEFT);
            content.getStyleClass().add("content");
        }

        @Override
        protected void updateItem(Stock stock, boolean empty) {
            super.updateItem(stock, empty);
            if (empty || stock == null) {
                setText(null);
                setGraphic(null);
            } else {
                symbol.setText(stock.getSymbol());
                meta.setText("Technology");
                setGraphic(content);
            }
        }

        // Trade button handler
        private void handleButtonClick(Stock stock) {
            System.out.println(stock.getSymbol());

            // 1. Spara vald aktie
            SelectedStockService.setSelectedStock(stock);

            // 2. Byt till stock view
            viewSwitcher.switchTo(EView.STOCKVIEW);
        }

    }
}
