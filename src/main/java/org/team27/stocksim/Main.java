
package org.team27.stocksim;

import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.ui.fx.EView;
import org.team27.stocksim.ui.fx.ViewSwitcher;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize the model and controller
        StockSim model = new StockSim();

        SimController controller = new SimController(model); // Dependency Injection

        // Initialize and launch the JavaFX view, set main view
        ViewSwitcher viewSwitcher = new ViewSwitcher(primaryStage, controller); // Dependency Injection
        viewSwitcher.switchTo(EView.MAINVIEW);

        primaryStage.setTitle("Stocksim");
        primaryStage.show();

    }

}
