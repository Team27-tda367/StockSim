package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.ui.fx.ViewSwitcher;

public abstract class ViewControllerBase {

    protected ISimController modelController;
    protected ViewSwitcher viewSwitcher;

    public void init(ISimController modelController, ViewSwitcher viewSwitcher) {
        this.modelController = modelController;
        this.viewSwitcher = viewSwitcher;
        onInit();
    }

    // Hook-metod som barnklasser kan överskugga
    protected void onInit() {
    }
}
