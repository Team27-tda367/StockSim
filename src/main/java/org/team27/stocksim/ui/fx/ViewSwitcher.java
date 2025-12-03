package org.team27.stocksim.ui.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.ui.fx.viewControllers.ViewControllerBase;

public class ViewSwitcher {

    private final Stage stage;
    private final ISimController modelController;

    public ViewSwitcher(Stage primaryStage, ISimController simController) {
        this.stage = primaryStage;
        this.modelController = simController;
    }

    public void switchTo(EView view) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFileName()));
            Parent root = loader.load();

            // Get the viewcontroller and inject dependencies (modelController and this
            // ViewSwitcher)
            ViewControllerBase controller = loader.getController();
            controller.init(modelController, this);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace(); // TODO: Proper error handling
        }
    }

}
