package com.econnect.barangaymanagementapp.Controller.BarangayOffice;

import com.econnect.barangaymanagementapp.Controller.Base.HeaderBaseController;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.SceneManager;
import com.econnect.barangaymanagementapp.Utils.UserSession;
import javafx.fxml.FXML;

public class DashboardController extends HeaderBaseController  {

    private UserSession userSession;
    private SceneManager sceneManager;

    public DashboardController(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getLoginService().getUserSession();
        this.sceneManager = dependencyInjector.getSceneManager();
    }
    @FXML
    public void initialize() {
        setHeader("Barangay Office", "Welcome, " + userSession.getInstance().getCurrentEmployee().getFirstName());
    }


}
