package org.team27.stocksim.ui.fx;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Callback;

public class ViewSwitcher {
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

    public static void switchTo(View view) {
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
