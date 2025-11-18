package org.team27.stocksim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.team27.stocksim.model.db.Database;

public class Main extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        // Starta databasen innan UI
        Database.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/org/team27/stocksim/view/exampel.fxml"));

        Scene scene = new Scene(loader.load(), 400, 300);

        primaryStage.setTitle("Stocksim");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Stäng databasen snyggt
        Database.close();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
