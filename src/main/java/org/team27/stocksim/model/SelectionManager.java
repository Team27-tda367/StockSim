package org.team27.stocksim.model;

import org.team27.stocksim.dto.InstrumentDTO;

/**
 * Manages the currently selected stock in the UI.
 *
 * <p>SelectionManager provides a centralized way to track which stock the user
 * is currently viewing or interacting with in the UI. This enables coordination
 * between different view components without tight coupling.</p>
 *
 * <p><strong>Design Pattern:</strong> Singleton-like State Manager</p>
 * <ul>
 *   <li>Tracks single selected stock across application</li>
 *   <li>Provides query and mutation methods</li>
 *   <li>Supports selection clearing</li>
 *   <li>Enables UI coordination without direct view coupling</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * SelectionManager manager = new SelectionManager();
 *
 * // User clicks on a stock in the stock list
 * InstrumentDTO appleStock = stockMap.get("AAPL");
 * manager.setSelectedStock(appleStock);
 *
 * // Other views can query the selection
 * if (manager.hasSelection()) {
 *     InstrumentDTO selected = manager.getSelectedStock();
 *     detailView.display(selected);
 * }
 *
 * // Clear selection when appropriate
 * manager.clearSelection();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see org.team27.stocksim.dto.InstrumentDTO
 */
public class SelectionManager {

    /**
     * The currently selected stock, or null if none selected.
     */
    private InstrumentDTO selectedStock;

    /**
     * Gets the currently selected stock.
     *
     * @return The selected stock DTO, or null if none selected
     */
    public InstrumentDTO getSelectedStock() {
        return selectedStock;
    }

    /**
     * Sets the currently selected stock.
     *
     * @param stock The stock to select
     */
    public void setSelectedStock(InstrumentDTO stock) {
        this.selectedStock = stock;
    }

    /**
     * Clears the current selection.
     */
    public void clearSelection() {
        this.selectedStock = null;
    }

    /**
     * Checks if a stock is currently selected.
     *
     * @return true if a stock is selected, false otherwise
     */
    public boolean hasSelection() {
        return selectedStock != null;
    }
}
