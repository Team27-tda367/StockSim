package org.team27.stocksim.observer;

/**
 * Subject interface for the Observer pattern implementation.
 *
 * <p>This interface defines the contract for objects that can be observed
 * for state changes. Subjects maintain a list of observers and notify them
 * when significant changes occur. This is the "Subject" part of the Observer
 * design pattern.</p>
 *
 * <p><strong>Design Pattern:</strong> Observer (Subject role)</p>
 * <ul>
 *   <li>Manages observer registration and removal</li>
 *   <li>Maintains weak coupling between model and views</li>
 *   <li>Enables one-to-many dependency relationships</li>
 *   <li>Supports dynamic observer list at runtime</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Subject implementation
 * public class StockSim implements IModelSubject {
 *     private final List<IModelObserver> observers = new ArrayList<>();
 *
 *     @Override
 *     public void addObserver(IModelObserver obs) {
 *         observers.add(obs);
 *     }
 *
 *     @Override
 *     public void removeObserver(IModelObserver obs) {
 *         observers.remove(obs);
 *     }
 *
 *     private void notifyPriceUpdate(Map<String, InstrumentDTO> changed) {
 *         for (IModelObserver o : observers) {
 *             o.onPriceUpdate(changed);
 *         }
 *     }
 * }
 *
 * // Usage
 * StockSim model = new StockSim();
 * IModelObserver view = new StockView();
 * model.addObserver(view);
 * // Now view will be notified of model changes
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see IModelObserver
 * @see org.team27.stocksim.model.StockSim
 */
public interface IModelSubject {
    /**
     * Registers an observer to receive notifications of model changes.
     *
     * @param obs The observer to add
     */
    void addObserver(IModelObserver obs);

    /**
     * Unregisters an observer from receiving notifications.
     *
     * @param obs The observer to remove
     */
    void removeObserver(IModelObserver obs);
}
