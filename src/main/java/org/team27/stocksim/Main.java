
package org.team27.stocksim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.team27.stocksim.controller.Controller;
import org.team27.stocksim.model.db.Database;
import org.team27.stocksim.model.market.StockSim;

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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/team27/stocksim/view/exampel.fxml"));
        Parent root = loader.load();  // This auto-creates the controller

        Controller controller = loader.getController();
        controller.setModel(model);

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
