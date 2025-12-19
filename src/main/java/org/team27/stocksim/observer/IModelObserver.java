package org.team27.stocksim.observer;

import java.util.HashMap;

import org.team27.stocksim.dto.InstrumentDTO;

/**
 * Observer interface for receiving model change notifications.
 *
 * <p>This interface defines the contract for observing changes in the stock
 * simulation model. It implements the Observer pattern, allowing view components
 * to stay synchronized with model state without tight coupling. This supports
 * the MVC (Model-View-Controller) architecture of the application.</p>
 *
 * <p><strong>Design Pattern:</strong> Observer</p>
 * <ul>
 *   <li>Decouples model (StockSim) from views</li>
 *   <li>Enables multiple views to observe same model</li>
 *   <li>Push-style notifications with relevant data</li>
 *   <li>Supports fine-grained update notifications</li>
 * </ul>
 *
 * <h2>Notification Types:</h2>
 * <ul>
 *   <li>Stock changes - New stocks added or stock list modified</li>
 *   <li>Price updates - Stock prices changed due to trades</li>
 *   <li>Trade settled - A trade was successfully executed</li>
 *   <li>Portfolio changed - User portfolio was updated</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * public class StockView implements IModelObserver {
 *     @Override
 *     public void onPriceUpdate(HashMap<String, ? extends InstrumentDTO> stocks) {
 *         // Update price display for changed stocks
 *         for (var entry : stocks.entrySet()) {
 *             updatePriceLabel(entry.getKey(), entry.getValue().getPrice());
 *         }
 *     }
 *
 *     @Override
 *     public void onTradeSettled() {
 *         refreshTradeHistory();
 *     }
 *
 *     @Override
 *     public void onPortfolioChanged() {
 *         refreshPortfolioDisplay();
 *     }
 *
 *     @Override
 *     public void onStocksChanged(Object payload) {
 *         refreshStockList();
 *     }
 * }
 *
 * // Register observer
 * stockSim.addObserver(new StockView());
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see IModelSubject
 * @see org.team27.stocksim.model.StockSim
 */
public interface IModelObserver {

    /**
     * Called when the list of available stocks changes.
     *
     * @param payload Additional data about the change (implementation-specific)
     */
    void onStocksChanged(Object payload);

    /**
     * Called when stock prices are updated due to trades.
     *
     * <p>Only stocks that actually changed are included in the map,
     * allowing efficient incremental updates.</p>
     *
     * @param stocks Map of stock symbols to their updated DTOs
     */
    void onPriceUpdate(HashMap<String, ? extends InstrumentDTO> stocks);

    /**
     * Called when a trade is successfully settled.
     *
     * <p>This notification is fired after portfolios are updated and
     * the trade is recorded. Views can refresh trade history displays.</p>
     */
    void onTradeSettled();

    /**
     * Called when the current user's portfolio changes.
     *
     * <p>Triggered by stock purchases, sales, or balance changes.
     * Views should refresh portfolio and balance displays.</p>
     */
    void onPortfolioChanged();
}
