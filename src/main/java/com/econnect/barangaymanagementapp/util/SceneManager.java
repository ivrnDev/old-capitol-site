package com.econnect.barangaymanagementapp.util;

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
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root);
            } else {
                scene.setRoot(root);
            }
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to swith Scenes: " + e.getMessage());
            switchToDefaultScene();
        }
    }

    public void switchToDefaultScene() {
        switchScene("View/login.fxml");
    }


}
