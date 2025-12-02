
package org.team27.stocksim;

/* OLD */
import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.ui.fx.IView;
import org.team27.stocksim.ui.fx.JavaFXView;

public class Main {

    public static void main(String[] args) {
        IView view = new JavaFXView(); // this can be any form of view (modularity)
        StockSim model = new StockSim();
        SimController controller = new SimController(view, model);

        controller.start();

    }

}