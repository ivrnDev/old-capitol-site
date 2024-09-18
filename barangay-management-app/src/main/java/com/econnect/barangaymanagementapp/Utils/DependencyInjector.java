package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Interface.ControllerDependencies;
import javafx.stage.Stage;

public class DependencyInjector {
    private final SceneManager sceneManager;
    private final ModalUtils modalUtils;

    public DependencyInjector(Stage stage) {
        this.sceneManager = new SceneManager(stage, this);
        this.modalUtils = new ModalUtils(stage);
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public ModalUtils getModalUtils() {
        return modalUtils;
    }

    public void injectDependenciesToController(Object controller) {
        if (controller instanceof ControllerDependencies) {
            ((ControllerDependencies) controller).setDependencies(this);
        }
    }
}
