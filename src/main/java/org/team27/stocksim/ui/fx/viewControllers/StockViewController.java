package org.team27.stocksim.ui.fx.viewControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.team27.stocksim.model.market.Stock;
import org.team27.stocksim.ui.fx.EView;
import org.team27.stocksim.ui.fx.ViewSwitcher;
import org.team27.stocksim.ui.fx.SelectedStockService;

import java.math.BigDecimal;

public class StockViewController {

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

    private boolean isFavorite = false;
    private Stock stock;

    @FXML
    private void initialize() {
        // Hämta vald aktie från service
        stock = SelectedStockService.getSelectedStock();

        if (stock != null) {
            // Symbol
            symbolLabel.setText(stock.getSymbol());

            // Namn/beskrivning – anpassa efter din Stock-modell
            // Om ni har t.ex. getName() / getFullName():
            nameLabel.setText(stock.getName());

            // Pris – anpassa efter vad modellen har (getCurrentPrice, getLastPrice, etc.)
            BigDecimal price = stock.getTickSize(); // EXEMPEL – byt till rätt getter
            if (priceLabel != null) {
                priceLabel.setText(price.toString());
            }
            if (orderPriceLabel != null) {
                orderPriceLabel.setText(price + " SEK");
            }
        }
    }

    @FXML
    public void onExample(ActionEvent event) {
        ViewSwitcher.switchTo(EView.CREATESTOCK);
    }

    @FXML
    public void onMainView(ActionEvent event) {
        ViewSwitcher.switchTo(EView.MAINVIEW);
    }

    @FXML
    private void onToggleFavorite() {
        isFavorite = !isFavorite;
        favoriteIcon.setText(isFavorite ? "★" : "☆");
    }
}
