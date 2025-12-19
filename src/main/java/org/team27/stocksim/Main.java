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
        // Parse command-line arguments
        boolean simMode = false;
        boolean displayMode = false;

        for (String arg : args) {
            if ("-sim".equals(arg)) {
                simMode = true;
            } else if ("-display".equals(arg)) {
                displayMode = true;
            }
        }

        // If no arguments provided, show usage
        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println(
                    "  mvn exec:java -Dexec.args=\"-sim\"       - Run headless simulation to generate price data");
            System.out.println("  mvn exec:java -Dexec.args=\"-display\"   - Load existing data and show JavaFX UI");

            displayMode = true;
        }

        int simulationSpeed;
        // Simulation configuration
        if (displayMode) {
            simulationSpeed = 1; // 1 real second = 1 day simulated time
        } else {
            simulationSpeed = 3600;
        }
        int tickInterval = 50; // Check for new simulation seconds every 50ms
        int durationInRealSeconds = 8; // Run fast simulation for 8 seconds

        if (simMode) {
            System.out.println("Running simulation for " + durationInRealSeconds + " real seconds at speedup factor "
                    + simulationSpeed + "...");
        }

        // Initialize the model with simulation configuration
        StockSim model = new StockSim(simulationSpeed, tickInterval, durationInRealSeconds);
        ISimController controller = new SimController(model);

        // Set up initial data (stocks, bots, positions)
        SimSetup setup = new SimSetup(model);

        if (displayMode) {
            // Load pre-generated price data from JSON
            setup.startWithLoadedPrices();

            // Launch the JavaFX UI
            launchUI(args, model, controller);
        } else if (simMode) {
            setup.start();

            // Run simulation to generate new price data
            try {
                Thread.sleep((durationInRealSeconds) * 1000);
            } catch (InterruptedException e) {
                System.err.println("Simulation interrupted: " + e.getMessage());
            }
            // Save generated price data
            model.saveStockPrices();
            model.saveBotPositions();
            model.stopMarketSimulation();
            System.out.println("Simulation completed. Price data and bot positions saved.");
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
