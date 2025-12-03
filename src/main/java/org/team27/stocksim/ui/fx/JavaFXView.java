package org.team27.stocksim.ui.fx;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

import java.util.HashMap;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.model.market.Instrument;
import org.team27.stocksim.ui.IView;

public class JavaFXView extends Application implements IView {
    private static ISimController staticModelController;
    protected HashMap<String, Instrument> stocks;

    // All the other methods from IView can be implemented here as needed
    @Override
    public void newStockCreated(HashMap<String, Instrument> stocks) {
        this.stocks = stocks;
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

        ViewSwitcher viewSwitcher = new ViewSwitcher(primaryStage, staticModelController, this);
        viewSwitcher.switchTo(EView.MAINVIEW);

        primaryStage.setTitle("Stocksim");
        primaryStage.show();

    }

}
