package org.team27.stocksim.ui;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.observer.ModelObserver;

public interface IView extends ModelObserver {

    void setController(ISimController controller);

    void show();

}
