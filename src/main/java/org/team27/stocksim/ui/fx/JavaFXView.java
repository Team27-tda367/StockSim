package org.team27.stocksim.ui.fx;

import javafx.application.Application;
import javafx.stage.Stage;
import java.util.HashMap;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.model.market.Instrument;
import org.team27.stocksim.ui.IViewInit;

public class JavaFXView extends Application implements IViewInit {
    private static ISimController staticModelController;
    protected HashMap<String, Instrument> stocks;

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
