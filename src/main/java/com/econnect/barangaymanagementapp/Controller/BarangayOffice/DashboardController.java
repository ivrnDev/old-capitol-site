package com.econnect.barangaymanagementapp.Controller.BarangayOffice;

import com.econnect.barangaymanagementapp.Controller.Base.HeaderBaseController;
import com.econnect.barangaymanagementapp.Interface.ControllerDependencies;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.UserSession;
import javafx.fxml.FXML;

public class DashboardController extends HeaderBaseController  implements ControllerDependencies {

    private UserSession userSession;

    @Override
    public void setDependencies(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getLoginService().getUserSession();
    }

    @FXML
    public void initialize() {
        setHeader("Barangay Office", "Welcome, " + userSession.getInstance().getCurrentEmployee().getFirstName());
    }

}
