package com.econnect.barangaymanagementapp;

import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        DependencyInjector dependencyInjector = new DependencyInjector(stage);
        stage.initStyle(StageStyle.DECORATED); // For testing purposes, convert to UNDECORATED
        SceneManager sceneManager = dependencyInjector.getSceneManager();
        sceneManager.switchToDefaultScene();
    }

    public static void main(String[] args) {
        launch(args);
    }

}