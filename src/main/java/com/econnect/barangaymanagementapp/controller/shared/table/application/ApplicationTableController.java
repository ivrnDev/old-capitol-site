package com.econnect.barangaymanagementapp.controller.shared.table.application;

import com.econnect.barangaymanagementapp.controller.shared.ApplicationController;
import com.econnect.barangaymanagementapp.controller.shared.base.BaseTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.EMPLOYEE_APPLICATION_ROW;

public class ApplicationTableController extends BaseTableController<Employee> {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;
    private final ApplicationController applicationController;

    public ApplicationTableController(DependencyInjector dependencyInjector, ApplicationController applicationController) {
        super(dependencyInjector);
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
        this.applicationController = applicationController;
    }

    @Override
    public void addRow(Employee employeeData) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(EMPLOYEE_APPLICATION_ROW.getFxmlPath(), dependencyInjector, applicationController);
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