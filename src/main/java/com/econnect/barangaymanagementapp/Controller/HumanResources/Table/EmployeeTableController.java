package com.econnect.barangaymanagementapp.Controller.HumanResources.Table;

import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.Roles;
import com.econnect.barangaymanagementapp.Enumeration.Status;
import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class EmployeeTableController {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;

    public EmployeeTableController(DependencyInjector dependencyInjector) {
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    public void addEmployeeRow(String employeeId, String lastName, String firstName, Roles role, Departments department, Status.EmployeeStatus status, String imageUrl) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader("View/HumanResources/Table/employee-row.fxml");
            HBox employeeRow = loader.load();
            EmployeeRowController rowController = loader.getController();
            rowController.setEmployeeData(
                    employeeId,
                    lastName,
                    firstName,
                    role.getName(),
                    department.getName(),
                    status.getName(),
                    imageUrl.isEmpty() ? new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/default-profile.png"))) : new Image(imageUrl)
            );
            tableContent.getChildren().add(employeeRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace(); // Print the exception for debugging
            throw new RuntimeException("Error adding employee row: " + e.getMessage(), e);

        }
    }
}
