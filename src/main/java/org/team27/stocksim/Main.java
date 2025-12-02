
package org.team27.stocksim;

/* OLD */
import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.ui.fx.IView;
import org.team27.stocksim.ui.fx.JavaFXView;

public class Main {

    public static void main(String[] args) {
        IView view = new JavaFXView(); // this can be any form of view (modularity)

        SimController controller = new SimController(view);

        controller.start();

    }

}