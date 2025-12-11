package org.team27.stocksim;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.view.fx.FXStockSimApp;

import javafx.application.Application;

/**
 * Main entry point for the StockSim application.
 * Initializes the model and controller, then launches the JavaFX application.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize the model and controller
        StockSim model = new StockSim();
        ISimController controller = new SimController(model);

        controller.setUpSimulation();

        // Wait a moment to ensure setup is complete
        System.out.println("Running simulation for 10 seconds...");
        try {
            Thread.sleep(10000); // 10 seconds

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Pass to JavaFX application and launch
        FXStockSimApp.setModelAndController(model, controller);
        Application.launch(FXStockSimApp.class, args);
    }

}
