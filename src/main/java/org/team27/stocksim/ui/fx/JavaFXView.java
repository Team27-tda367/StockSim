package org.team27.stocksim.ui.fx;

import javafx.application.Application;
import javafx.stage.Stage;

import org.team27.stocksim.controller.ISimController;

public class JavaFXView extends Application {
    private static ISimController modelController;

    public void launchApp(ISimController controller) {
        modelController = controller;
        new Thread(() -> Application.launch(JavaFXView.class)).start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(modelController);
        ViewSwitcher viewSwitcher = new ViewSwitcher(primaryStage, modelController);
        viewSwitcher.switchTo(EView.MAINVIEW);

        primaryStage.setTitle("Stocksim");
        primaryStage.show();

    }

}
