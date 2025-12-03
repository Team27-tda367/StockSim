
package org.team27.stocksim;

/* OLD */
import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.ui.fx.JavaFXView;
import org.team27.stocksim.ui.fx.ViewSwitcher;

public class Main {

    public static void main(String[] args) {
        StockSim model = new StockSim();

        SimController controller = new SimController(model); // Dependency Injection

        JavaFXView view = new JavaFXView();

        view.launchApp(controller);

    }

}