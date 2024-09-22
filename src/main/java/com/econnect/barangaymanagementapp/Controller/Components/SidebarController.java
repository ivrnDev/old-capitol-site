package com.econnect.barangaymanagementapp.Controller.Components;

import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.SceneManager;
import com.econnect.barangaymanagementapp.Utils.UserSession;
import javafx.fxml.FXML;

public class SidebarController {

    private final UserSession userSession;
    private final SceneManager sceneManager;


    public SidebarController(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getUserSession();
        this.sceneManager = dependencyInjector.getSceneManager();
    }

    public void initialize() {
    }

    @FXML
    public void logout() {
        userSession.clearSession();
        sceneManager.switchScene("View/login.fxml");
    }
}
