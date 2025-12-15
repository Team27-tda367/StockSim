package org.team27.stocksim.view.fx;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.util.dto.InstrumentDTO;

public class SelectedStockService {

    private static InstrumentDTO selectedStock;

    public static void setSelectedStock(InstrumentDTO stock) {
        selectedStock = stock;
    }

    public static InstrumentDTO getSelectedStock() {
        return selectedStock;
    }
}
