package com.econnect.barangaymanagementapp.Controller.Components;

import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.SceneManager;
import com.econnect.barangaymanagementapp.Utils.UserSession;
import javafx.fxml.FXML;

public class SidebarController {

    private UserSession userSession;
    private SceneManager sceneManager;


    public SidebarController(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getLoginService().getUserSession();
        this.sceneManager = dependencyInjector.getSceneManager();
    }

    public void initialize() {
    }

    @FXML
    public void logout() {
        userSession.getInstance().clearSession();
        sceneManager.switchScene("View/login.fxml");
    }
}
