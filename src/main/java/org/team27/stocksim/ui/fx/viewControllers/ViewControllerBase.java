package org.team27.stocksim.ui.fx.viewControllers;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.ui.IView;
import org.team27.stocksim.ui.fx.ViewSwitcher;

public abstract class ViewControllerBase {

    protected ISimController modelController;
    protected ViewSwitcher viewSwitcher;
    protected IView parentView;

    public void init(ISimController modelController, ViewSwitcher viewSwitcher, IView parentView) {
        this.modelController = modelController;
        this.viewSwitcher = viewSwitcher;
        this.parentView = parentView;
        onInit();
    }

    // Hook-metod som barnklasser kan överskugga
    protected void onInit() {
    }
}
