package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.observer.ModelEvent;
import org.team27.stocksim.ui.fx.EView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import org.team27.stocksim.model.market.Instrument;
import org.team27.stocksim.model.market.Stock;

import org.team27.stocksim.ui.fx.SelectedStockService;

import java.math.BigDecimal;
import java.util.HashMap;

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

    private boolean isFavorite = false;
    private Instrument stock;

    /**
     * Handler for the header "Home" button (FXML references `onExample`).
     * Navigates back to the main view.
     */

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
        viewSwitcher.switchTo(EView.CREATESTOCK);
    }

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

    }

    @Override
    protected void onInit() {
        modelController.addObserver(this);
    }

}
