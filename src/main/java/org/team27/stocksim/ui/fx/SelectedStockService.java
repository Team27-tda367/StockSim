package org.team27.stocksim.ui.fx;

import org.team27.stocksim.model.market.Instrument;

public class SelectedStockService {

    private static Instrument selectedStock;

    public static void setSelectedStock(Instrument stock) {
        selectedStock = stock;
    }

    public static Instrument getSelectedStock() {
        return selectedStock;
    }
}
