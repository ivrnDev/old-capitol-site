package com.econnect.barangaymanagementapp.Controller.Component;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class HeaderController {

    private final UserSession userSession;

    @FXML
    private Text headerTitle;

    @FXML
    private Text greetingText;

    public HeaderController(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getUserSession();
    }

    public void initialize() {
        Employee currentEmployee = userSession.getCurrentEmployee();
        headerTitle.setText(currentEmployee.getDepartment().getName());
        greetingText.setText("Welcome, " + userSession.getCurrentEmployee().getFirstName());
    }
}
