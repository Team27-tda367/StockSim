package org.team27.stocksim.ui.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

public class JavaFXView extends Application implements IView {

    private static JavaFXView instance;

    private IViewControllerAdapter controllerAdapter = () -> { };

    @Override
    public void newStockCreated(HashMap stocks) {

    }

    @Override
    public void setControllerAdapter(IViewControllerAdapter adapter) {
        this.controllerAdapter = adapter;
    }

    @Override
    public void show() {
        new Thread(() -> Application.launch(JavaFXView.class)).start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

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
}
