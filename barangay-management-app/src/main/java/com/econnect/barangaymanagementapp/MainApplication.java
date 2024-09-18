package com.econnect.barangaymanagementapp;

import com.econnect.barangaymanagementapp.Utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {

    private SceneManager sceneManager;

    @Override
    public void start(Stage stage) throws IOException {
        stage.initStyle(StageStyle.UNDECORATED);
        sceneManager = new SceneManager(stage);
        sceneManager.switchScene("View/login.fxml");
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public static void main(String[] args) {
        launch(args);
    }

}