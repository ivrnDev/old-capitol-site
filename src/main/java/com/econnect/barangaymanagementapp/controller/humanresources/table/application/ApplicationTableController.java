package com.econnect.barangaymanagementapp.controller.humanresources.table.application;

import com.econnect.barangaymanagementapp.controller.humanresources.ApplicationsController;
import com.econnect.barangaymanagementapp.controller.shared.BaseTableController;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.ZonedDateTime;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.HR_EMPLOYEE_APPLICATION_ROW;

public class ApplicationTableController extends BaseTableController {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;
    private final ApplicationsController applicationsController;

    public ApplicationTableController(DependencyInjector dependencyInjector, ApplicationsController applicationsController) {
        super(dependencyInjector);
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
        this.applicationsController = applicationsController;
    }

    @Override
    protected void addEmployeeApplicationRow(String employeeId, String lastName, String firstName, EmployeeStatus status, ApplicationType type, ZonedDateTime zonedDate, String imageUrl) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(HR_EMPLOYEE_APPLICATION_ROW.getFxmlPath(), dependencyInjector, applicationsController);
            HBox applicationRow = loader.load();
            ApplicationRowController applicationRowController = loader.getController();
            applicationRowController.setEmployeeData(employeeId, lastName, firstName, status.getName(), type.getName(), DateFormatter.extractDateAndFormat(zonedDate), DateFormatter.extractTimeAndFormat(zonedDate), getImageOrDefault(employeeId));
            super.loadImage(employeeId, imageUrl, applicationRowController);
            tableContent.getChildren().add(applicationRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding employee row: " + e.getMessage(), e);
        }
    }
}
