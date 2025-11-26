package org.team27.stocksim.controller;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ViewSwitcher {
    private static Scene scene;
    public static void setScene(Scene scene){
        ViewSwitcher.scene = scene;
    }
    public static void switchTo(View view){
        if (scene == null){
            System.out.println("No scene was set");
            return;
        }
        try {
            Parent root = FXMLLoader.load(
                ViewSwitcher.class.getResource((view.getFileName())
        ));

        scene.setRoot(root);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
}
