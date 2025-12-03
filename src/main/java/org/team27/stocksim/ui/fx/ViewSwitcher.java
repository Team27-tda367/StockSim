package org.team27.stocksim.ui.fx;

import java.io.IOException;

import javax.swing.text.View;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.ui.fx.viewControllers.ViewControllerBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Callback;

/* public class ViewSwitcher {
    private static Scene scene;
    private static Callback<Class<?>, Object> controllerFactory;

    public static void setScene(Scene scene) {
        ViewSwitcher.scene = scene;
    }

    public static void setControllerFactory(Callback<Class<?>, Object> factory) {
        ViewSwitcher.controllerFactory = factory;
    }

    public static Callback<Class<?>, Object> getControllerFactory() {
        return controllerFactory;
    }

    public static void switchTo(EView view) {
        if (scene == null) {
            System.out.println("No scene was set");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    ViewSwitcher.class.getResource(view.getFileName()));

            // Use the controller factory if available
            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory);
            }

            Parent root = loader.load();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

 */

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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
