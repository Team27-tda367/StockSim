package org.team27.stocksim.ui.fx;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.HashMap;

import org.team27.stocksim.controller.ISimController;

public class JavaFXView extends Application implements IView {
    private static ISimController staticModelController;

    @Override
    public void newStockCreated(HashMap stocks) {
        // Implementation for updating the view when a new stock is created
        System.out.println("New stock created: " + stocks);
    }

    @Override
    public void setController(ISimController controller) {
        staticModelController = controller;
    }

    public void show() {
        new Thread(() -> Application.launch(JavaFXView.class)).start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Get the controller from the static field

        ViewSwitcher viewSwitcher = new ViewSwitcher(primaryStage, staticModelController);
        viewSwitcher.switchTo(EView.MAINVIEW);

        primaryStage.setTitle("Stocksim");
        primaryStage.show();

    }
}
