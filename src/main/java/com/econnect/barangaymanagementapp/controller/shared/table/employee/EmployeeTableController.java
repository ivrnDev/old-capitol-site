package com.econnect.barangaymanagementapp.controller.shared.table.employee;

import com.econnect.barangaymanagementapp.controller.shared.EmployeeController;
import com.econnect.barangaymanagementapp.controller.shared.base.BaseTableController;
import com.econnect.barangaymanagementapp.controller.shared.table.application.ApplicationRowController;
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

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.EMPLOYEE_ROW;

public class EmployeeTableController extends BaseTableController<Employee> {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;

    public EmployeeTableController(DependencyInjector dependencyInjector, EmployeeController employeeController) {
        super(dependencyInjector);
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
    }

    @Override
    public void addRow(Employee employeeData) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(EMPLOYEE_ROW.getFxmlPath(), dependencyInjector);
            HBox employeeRow = loader.load();
            EmployeeRowController employeeRowController = loader.getController();
            Image defaultImage = super.getImageOrDefault(employeeData.getId());
            employeeRow.setUserData(employeeRowController);
            employeeRowController.setImage(defaultImage);
            employeeRowController.setData(employeeData);
            super.loadImage(employeeData.getId(), employeeData.getProfileUrl(), employeeRowController);
            tableContent.getChildren().add(employeeRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding employee row: " + e.getMessage(), e);
        }
    }

    public void updateRow(Employee updatedEmployee) {
        boolean rowExist = false;
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox employeeRow) {
                EmployeeRowController rowController = (EmployeeRowController) employeeRow.getUserData();
                if (rowController != null && rowController.getEmployeeId().equals(updatedEmployee.getId())) {
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
            if (node instanceof HBox employeeRow) {
                EmployeeRowController rowController = (EmployeeRowController) employeeRow.getUserData();
                if (rowController.getEmployeeId().equals(employeeId)) {
                    tableContent.getChildren().remove(employeeRow);
                    break;
                }
            }
        }

        if (tableContent.getChildren().isEmpty()) {
            super.showNoData();
        }
    }
}
