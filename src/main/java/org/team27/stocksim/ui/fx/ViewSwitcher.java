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

            // Hämta controller och injicera domain + switcher
            Object controller = loader.getController();
            if (controller instanceof ViewControllerBase baseController) {
                baseController.init(modelController, this);
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace(); // i riktig kod: logga snyggare
        }
    }

}
