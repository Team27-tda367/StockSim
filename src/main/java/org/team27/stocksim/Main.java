package org.team27.stocksim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.team27.stocksim.controller.SimController;
import org.team27.stocksim.controller.SimControllerImpl;
import org.team27.stocksim.model.db.Database;
import org.team27.stocksim.model.market.StockSim;
import org.team27.stocksim.ui.fx.MainViewController;
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

        // Create FXML loader
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/team27/stocksim/view/exampel.fxml"));

        loader.setControllerFactory(type -> {
            if (type == MainViewController.class) {
                return new MainViewController(simController, viewAdapter);
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Load FXML. The controller is created by the factory
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
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