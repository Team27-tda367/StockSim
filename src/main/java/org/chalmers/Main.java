package org.chalmers; // viktigt att det stämmer med din klass

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.chalmers.db.Database;

public class Main extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        // Starta databasen innan UI
        Database.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/org/chalmers/ui/fxml_example.fxml"));

        Scene scene = new Scene(loader.load(), 400, 300);
        primaryStage.setTitle("JavaFX + SQLite + ORMLite");
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
