package com.econnect.barangaymanagementapp.controller.table.employee;

import com.econnect.barangaymanagementapp.controller.EmployeeApplicationController;
import com.econnect.barangaymanagementapp.controller.base.BaseTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.EMPLOYEE_APPLICATION_ROW;

public class EmployeeApplicationTableController extends BaseTableController<Employee> {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;
    private final EmployeeApplicationController employeeApplicationController;

    public EmployeeApplicationTableController(DependencyInjector dependencyInjector, EmployeeApplicationController employeeApplicationController) {
        super(dependencyInjector);
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
        this.employeeApplicationController = employeeApplicationController;
    }

    @Override
    public void addRow(Employee employeeData) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(EMPLOYEE_APPLICATION_ROW.getFxmlPath(), dependencyInjector, employeeApplicationController);
            HBox applicationRow = loader.load();
            EmployeeApplicationRowController applicationRowController = loader.getController();
            applicationRow.setUserData(applicationRowController);
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

    public void updateRow(Employee updatedEmployee) {
        boolean rowExist = false;
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox applicationRow) {
                EmployeeApplicationRowController rowController = (EmployeeApplicationRowController) applicationRow.getUserData();
                if (rowController != null && rowController.getResidentId().equals(updatedEmployee.getId())) {
                    rowController.setData(updatedEmployee);
                    rowExist = true;
                    break;
                }
            }
        }

        if (!rowExist) {
            super.removeNoDataRow();
            addRow(updatedEmployee);
        }
    }

    public void deleteRow(String employeeId) {
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox applicationRow) {
                EmployeeApplicationRowController rowController = (EmployeeApplicationRowController) applicationRow.getUserData();
                if (rowController != null && rowController.getResidentId().equals(employeeId)) {
                    tableContent.getChildren().remove(applicationRow);
                    break;
                }
            }
        }

        if (tableContent.getChildren().isEmpty()) {
            super.showNoData();
        }
    }
}
