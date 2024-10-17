package com.econnect.barangaymanagementapp.controller.humanresources.table.application;

import com.econnect.barangaymanagementapp.controller.humanresources.ApplicationsController;
import com.econnect.barangaymanagementapp.controller.shared.BaseTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.HR_EMPLOYEE_APPLICATION_ROW;

public class ApplicationTableController extends BaseTableController<Employee> {
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
    protected void addRow(Employee employeeData) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(HR_EMPLOYEE_APPLICATION_ROW.getFxmlPath(), dependencyInjector, applicationsController);
            HBox applicationRow = loader.load();
            ApplicationRowController applicationRowController = loader.getController();
            Image defaultImage = super.getImageOrDefault(employeeData.getId());
            applicationRowController.setImage(defaultImage);
            applicationRowController.setData(employeeData);
            super.loadImage(employeeData.getId(), employeeData.getProfileUrl(), applicationRowController);
            tableContent.getChildren().add(applicationRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding employee row: " + e.getMessage(), e);
        }
    }
}
