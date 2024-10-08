package com.econnect.barangaymanagementapp.controller.component;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.state.UserSession;
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

        if (currentEmployee != null) {
            headerTitle.setText(currentEmployee.getDepartment().getName());
            greetingText.setText("Welcome, " + currentEmployee.getFirstName());
        } else {
            headerTitle.setText("No Department");
            greetingText.setText("No user logged in.");
        }
    }
}
