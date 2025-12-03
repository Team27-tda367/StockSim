
package org.team27.stocksim;

/* OLD */
import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.ui.IViewInit;
import org.team27.stocksim.ui.fx.JavaFXView;

public class Main {

    public static void main(String[] args) {
        StockSim model = new StockSim();

        SimController controller = new SimController(model); // Dependency Injection
        IViewInit view = new JavaFXView();
        view.setController(controller);

        // Create viewControllers and link them to the model as observers

        // model.addObserver(view);

        view.show();
    }

}