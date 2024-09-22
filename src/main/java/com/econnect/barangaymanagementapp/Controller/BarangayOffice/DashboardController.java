package com.econnect.barangaymanagementapp.Controller.BarangayOffice;

import com.econnect.barangaymanagementapp.Controller.Base.HeaderBaseController;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.UserSession;
import javafx.fxml.FXML;

public class DashboardController extends HeaderBaseController  {

    private final UserSession userSession;

    public DashboardController(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getUserSession();
    }
    @FXML
    public void initialize() {
        setHeader("Barangay Office", "Welcome, " + userSession.getCurrentEmployee().getFirstName());
    }


}
