package org.team27.stocksim.model;

import org.team27.stocksim.dto.InstrumentDTO;


public class SelectionManager {

    private InstrumentDTO selectedStock;

    public InstrumentDTO getSelectedStock() {
        return selectedStock;
    }

    public void setSelectedStock(InstrumentDTO stock) {
        this.selectedStock = stock;
    }

    public void clearSelection() {
        this.selectedStock = null;
    }

    public boolean hasSelection() {
        return selectedStock != null;
    }
}
