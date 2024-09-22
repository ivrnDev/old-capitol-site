package com.econnect.barangaymanagementapp.Controller.HR;

import com.econnect.barangaymanagementapp.Controller.Base.HeaderBaseController;
import com.econnect.barangaymanagementapp.Interface.ControllerDependencies;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.UserSession;
import javafx.fxml.FXML;

public class DashboardController extends HeaderBaseController  {

    private UserSession userSession;

    public DashboardController(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getLoginService().getUserSession();
    }

    @FXML
    public void initialize() {
        setHeader("Human Resource", "Welcome, " + userSession.getInstance().getCurrentEmployee().getFirstName());
    }


}
