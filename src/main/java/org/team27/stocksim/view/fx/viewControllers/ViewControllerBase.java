package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.view.ViewAdapter;
import org.team27.stocksim.view.fx.ViewSwitcher;

public abstract class ViewControllerBase {

    protected ISimController modelController;
    protected ViewSwitcher viewSwitcher;
    protected ViewAdapter viewAdapter;

    public void init(ISimController modelController, ViewSwitcher viewSwitcher, ViewAdapter viewAdapter) {
        this.modelController = modelController;
        this.viewSwitcher = viewSwitcher;
        this.viewAdapter = viewAdapter;
        onInit();
    }

    // Hook-metod som barnklasser kan överskugga
    protected abstract void onInit();

}
