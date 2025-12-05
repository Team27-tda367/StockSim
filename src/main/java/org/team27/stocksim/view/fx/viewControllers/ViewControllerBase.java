package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.view.ViewAdapter;
import org.team27.stocksim.view.fx.ViewSwitcher;

public abstract class ViewControllerBase {

    protected ISimController modelController;
    protected ViewSwitcher viewSwitcher;
    protected ViewAdapter viewAdapter;
    private boolean initialized = false;

    public void init(ISimController modelController, ViewSwitcher viewSwitcher, ViewAdapter viewAdapter) {
        this.modelController = modelController;
        this.viewSwitcher = viewSwitcher;
        this.viewAdapter = viewAdapter;

        // Call onInit only the first time
        if (!initialized) {
            onInit();
            initialized = true;
        }

        // Call onShow every time the view is displayed
        onShow();
    }

    // Hook method for one-time initialization
    protected abstract void onInit();

    // Hook method called every time the view is shown (can be overridden)
    protected void onShow() {
        // Default: do nothing. Subclasses can override to refresh data.
    }

}
