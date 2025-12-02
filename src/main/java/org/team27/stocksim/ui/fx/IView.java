package org.team27.stocksim.ui.fx;

import org.team27.stocksim.observer.ModelObserver;

public interface IView extends ModelObserver {
    void setControllerAdapter(IViewControllerAdapter adapter);
    void show();
}
