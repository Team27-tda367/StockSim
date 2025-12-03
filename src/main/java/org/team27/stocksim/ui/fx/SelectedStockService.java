package org.team27.stocksim.ui.fx;

import org.team27.stocksim.model.market.Stock;

public class SelectedStockService {

    private static Stock selectedStock;

    public static void setSelectedStock(Stock stock) {
        selectedStock = stock;
    }

    public static Stock getSelectedStock() {
        return selectedStock;
    }
}
