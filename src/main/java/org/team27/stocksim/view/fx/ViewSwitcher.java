package org.team27.stocksim.view.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.view.ViewAdapter;
import org.team27.stocksim.view.fx.viewControllers.ViewControllerBase;

public class ViewSwitcher {

    private final Stage stage;
    private final ISimController modelController;
    private final ViewAdapter viewAdapter;

    public ViewSwitcher(Stage primaryStage, ISimController simController, ViewAdapter viewAdapter) {
        this.stage = primaryStage;
        this.modelController = simController;
        this.viewAdapter = viewAdapter;
    }

    public void switchTo(EView view) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFileName()));
            Parent root = loader.load();

            // Get the viewcontroller and inject dependencies (modelController and this
            // ViewSwitcher)
            ViewControllerBase controller = loader.getController();
            controller.init(modelController, this, viewAdapter);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace(); // TODO: Proper error handling
        }
    }

}
