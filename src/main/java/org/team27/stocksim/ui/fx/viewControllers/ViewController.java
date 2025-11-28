package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.ui.fx.MainViewAdapter;

/**
 * Abstract base class for view controllers in the StockSim application.
 * All view controllers should extend this class to gain access to the
 * simulation controller and view adapter.
 */
public abstract class ViewController {
    protected final SimController simController;
    protected final MainViewAdapter viewAdapter;

    /**
     * Constructs a new ViewController with the required dependencies.
     * 
     * @param simController the simulation controller
     * @param viewAdapter   the view adapter
     */
    public ViewController(SimController simController, MainViewAdapter viewAdapter) {
        this.simController = simController;
        this.viewAdapter = viewAdapter;
    }

    /**
     * Gets the simulation controller for this view.
     * 
     * @return the SimController instance
     */
    public SimController getSimController() {
        return simController;
    }

    /**
     * Gets the view adapter for this view.
     * 
     * @return the MainViewAdapter instance
     */
    public MainViewAdapter getViewAdapter() {
        return viewAdapter;
    }
}
