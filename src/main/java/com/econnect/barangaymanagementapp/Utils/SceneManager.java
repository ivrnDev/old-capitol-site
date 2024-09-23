package com.econnect.barangaymanagementapp.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private final Stage stage;
    private final FXMLLoaderFactory fxmlLoaderFactory;

    public SceneManager(DependencyInjector dependencyInjector) {
        this.stage = dependencyInjector.getStage();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    public void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(fxmlPath);
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
