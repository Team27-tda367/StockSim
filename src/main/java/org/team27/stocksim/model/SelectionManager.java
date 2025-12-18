package org.team27.stocksim.model;

import org.team27.stocksim.model.util.dto.InstrumentDTO;

/**
 * Manages the currently selected stock in the application.
 * This is part of the model layer and represents application state.
 * Moved from view layer (SelectedStockService) to follow proper MVC structure.
 */
public class SelectionManager {

    private InstrumentDTO selectedStock;

    /**
     * Set the currently selected stock.
     * 
     * @param stock The stock to select, or null to clear selection
     */
    public void setSelectedStock(InstrumentDTO stock) {
        this.selectedStock = stock;
    }

    /**
     * Get the currently selected stock.
     * 
     * @return The selected stock, or null if no stock is selected
     */
    public InstrumentDTO getSelectedStock() {
        return selectedStock;
    }

    /**
     * Clear the current selection.
     */
    public void clearSelection() {
        this.selectedStock = null;
    }

    /**
     * Check if a stock is currently selected.
     * 
     * @return true if a stock is selected, false otherwise
     */
    public boolean hasSelection() {
        return selectedStock != null;
    }
}
