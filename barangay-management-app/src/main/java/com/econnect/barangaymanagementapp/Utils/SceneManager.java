package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.MainApplication;
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
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();
            dependencyInjector.injectDependenciesToController(controller);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
