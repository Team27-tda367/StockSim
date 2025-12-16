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
        // === WORKFLOW CONFIGURATION ===
        // Set to false for fast headless simulation that generates and saves price data
        // Set to true to load existing price data and view in UI
        boolean loadExistingPrices = true;

        // Simulation configuration
        // int simulationSpeed = 3600; // 1 real second = 1 hour simulated time
        int simulationSpeed = 5;
        int tickInterval = 50; // Check for new simulation seconds every 50ms
        int durationInRealSeconds = 10; // Run fast simulation for 10 seconds
        int initialBotCount = 1000; // Number of trading bots

        // Initialize the model with simulation configuration
        StockSim model = new StockSim(simulationSpeed, tickInterval, durationInRealSeconds);
        ISimController controller = new SimController(model);

        // Set up initial data (stocks, bots, positions)
        SimSetup setup = new SimSetup(model, initialBotCount);

        if (loadExistingPrices) {
            // Load pre-generated price data from JSON
            System.out.println("Loading existing price data...");
            setup.startWithLoadedPrices();

            // Launch the JavaFX UI
            launchUI(args, model, controller);
        } else {
            setup.start();

            // Run simulation to generate new price data
            System.out.println("Running simulation to generate price data...");
            // Sleep for duration to allow simulation to complete
            try {
                Thread.sleep((durationInRealSeconds + 1) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Save generated price data
            model.saveStockPrices();
            model.stopMarketSimulation();
            System.out.println("Price data saved to JSON");
            System.exit(0);
        }

    }

    private static void launchUI(String[] args, StockSim model, ISimController controller) {// Pass to JavaFX
                                                                                            // application and
        // launch
        FXStockSimApp.setModelAndController(model, controller);
        Application.launch(FXStockSimApp.class, args);
    }

}
