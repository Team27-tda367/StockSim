package org.team27.stocksim.ui;

import org.team27.stocksim.controller.ISimController;

public interface IViewInit {

    void setController(ISimController controller);

    void show();

}
