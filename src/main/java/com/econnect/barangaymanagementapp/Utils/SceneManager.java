package com.econnect.barangaymanagementapp.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private final Stage stage;
    private final DependencyInjector dependencyInjector;

    public SceneManager(Stage stage, DependencyInjector dependencyInjector) {
        this.stage = stage;
        this.dependencyInjector = dependencyInjector;
    }

    public void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = dependencyInjector.getLoader(fxmlPath);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setMaximized(!fxmlPath.contains("login.fxml"));
        } catch (IOException e) {
            System.err.println("Failed to switch Scenes: " + e.getMessage());
            switchToDefaultScene();
        }
    }

    public void switchToDefaultScene() {
        switchScene("View/login.fxml");
    }


}
