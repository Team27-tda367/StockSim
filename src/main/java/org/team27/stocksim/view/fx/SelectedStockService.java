package org.team27.stocksim.view.fx;

import org.team27.stocksim.model.instruments.Instrument;

public class SelectedStockService {

    private static Instrument selectedStock;

    public static void setSelectedStock(Instrument stock) {
        selectedStock = stock;
    }

    public static Instrument getSelectedStock() {
        return selectedStock;
    }
}
