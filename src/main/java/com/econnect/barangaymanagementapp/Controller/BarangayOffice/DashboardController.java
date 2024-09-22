package com.econnect.barangaymanagementapp.Controller.BarangayOffice;

import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.UserSession;
import javafx.fxml.FXML;

public class DashboardController {

    private final UserSession userSession;

    public DashboardController(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getUserSession();
    }

    @FXML
    public void initialize() {

    }


}
