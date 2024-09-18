package com.econnect.barangaymanagementapp;

import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {

    private DependencyInjector dependencyInjector;

    @Override
    public void start(Stage stage) throws IOException {
        dependencyInjector = new DependencyInjector(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        SceneManager sceneManager = dependencyInjector.getSceneManager();
        sceneManager.switchScene("View/login.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }

}