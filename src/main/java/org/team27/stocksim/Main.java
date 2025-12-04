package org.team27.stocksim;

import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.view.fx.StockSimApp;

import javafx.application.Application;

/**
 * Main entry point for the StockSim application.
 * Initializes the model and controller, then launches the JavaFX application.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize the model and controller
        StockSim model = new StockSim();
        SimController controller = new SimController(model);

        controller.setUpSimulation();

        // Pass to JavaFX application and launch
        StockSimApp.setModelAndController(model, controller);
        Application.launch(StockSimApp.class, args);
    }

}
