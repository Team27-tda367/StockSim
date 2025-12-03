
package org.team27.stocksim;

/* OLD */
import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.ui.IView;
import org.team27.stocksim.ui.fx.JavaFXView;
import org.team27.stocksim.ui.terminal.TerminalView;

public class Main {

    public static void main(String[] args) {
        StockSim model = new StockSim();

        SimController controller = new SimController(model); // Dependency Injection
        IView view = new JavaFXView();
        // IView view = new TerminalView();
        view.setController(controller);
        model.addObserver(view);

        view.show();
    }

}