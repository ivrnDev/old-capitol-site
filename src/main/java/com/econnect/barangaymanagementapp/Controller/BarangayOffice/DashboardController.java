package com.econnect.barangaymanagementapp.Controller.BarangayOffice;

import com.econnect.barangaymanagementapp.Controller.Base.HeaderBaseController;
import com.econnect.barangaymanagementapp.Controller.Components.HeaderController;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.UserSession;
import javafx.fxml.FXML;

public class DashboardController extends HeaderBaseController {
    @FXML
    private HeaderController headerController;
    private UserSession userSession;
    @FXML
    public void initialize() {
        headerController.setHeaderTitle("Barangay Office");
        headerController.setGreetingText("Welcome, " + UserSession.getInstance().getCurrentEmployee().getFirstName());
    }

    @Override
    public void setDependencies(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getLoginService().getUserSession();
    }

}
