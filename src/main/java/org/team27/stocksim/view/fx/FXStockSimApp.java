package org.team27.stocksim.view.fx;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.view.ViewAdapter;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX Application entry point for StockSim.
 * Handles the initialization and wiring of the view components.
 */
public class FXStockSimApp extends Application {

    private static StockSim model;
    private static ISimController controller;

    /**
     * Set the model and controller before launching the application.
     * This must be called before Application.launch().
     */
    public static void setModelAndController(StockSim stockSim, ISimController simController) {
        model = stockSim;
        controller = simController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create ViewAdapter and register it with the model
        ViewAdapter viewAdapter = new ViewAdapter();
        model.addObserver(viewAdapter);

        // Initialize and launch the JavaFX view
        ViewSwitcher viewSwitcher = new ViewSwitcher(primaryStage, controller, viewAdapter);
        viewSwitcher.switchTo(EView.MAINVIEW);

        primaryStage.setTitle("Stocksim");
        primaryStage.show();
    }
}
