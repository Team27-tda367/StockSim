
package org.team27.stocksim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* OLD */
import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.controller.SimControllerImpl;
import org.team27.stocksim.model.db.Database;
import org.team27.stocksim.model.market.StockSim;
import org.team27.stocksim.ui.fx.ViewSwitcher;
import org.team27.stocksim.ui.fx.viewControllers.CreateStockPageController;
import org.team27.stocksim.ui.fx.viewControllers.MainViewController;
import org.team27.stocksim.ui.fx.MainViewAdapter;

public class Main extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        // Starta databasen innan UI
        Database.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // creating model
        StockSim model = new StockSim();

        // Create fascade controller for model
        SimController simController = new SimControllerImpl(model);

        // Create ViewAdapter
        MainViewAdapter viewAdapter = new MainViewAdapter(model);

        // Set up shared controller factory for all views
        ViewSwitcher.setControllerFactory(type -> {
            if (type == MainViewController.class) {
                return new MainViewController(simController, viewAdapter);
            }
            if (type == CreateStockPageController.class) {
                return new CreateStockPageController(simController, viewAdapter);
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Create FXML loader for initial view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/team27/stocksim/view/main_view.fxml"));
        loader.setControllerFactory(ViewSwitcher.getControllerFactory());

        // Load FXML. The controller is created by the factory
        Parent root = loader.load();

        Scene scene = new Scene(root);
        ViewSwitcher.setScene(scene);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Stocksim");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Stäng databasen snyggt
        Database.close();
        super.stop();
    }

    public static void main(String[] args) {
        // start Java-FX app
        launch(args);
    }

}