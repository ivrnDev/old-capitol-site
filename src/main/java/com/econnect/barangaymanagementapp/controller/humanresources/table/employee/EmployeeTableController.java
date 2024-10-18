package com.econnect.barangaymanagementapp.controller.humanresources.table.employee;

import com.econnect.barangaymanagementapp.controller.humanresources.EmployeeController;
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

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.HR_EMPLOYEE_ROW;

public class EmployeeTableController extends BaseTableController<Employee> {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;
    private final EmployeeController employeeController;

    public EmployeeTableController(DependencyInjector dependencyInjector, EmployeeController employeeController) {
        super(dependencyInjector);
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
        this.employeeController = employeeController;
    }

    @Override
    public void addRow(Employee employeeData) {
        System.out.println("Adding employee row: " + employeeData);
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(HR_EMPLOYEE_ROW.getFxmlPath(), dependencyInjector, employeeController);
            HBox employeeRow = loader.load();
            EmployeeRowController employeeRowController = loader.getController();
            Image defaultImage = super.getImageOrDefault(employeeData.getId());
            employeeRowController.setImage(defaultImage);
            employeeRowController.setData(employeeData);
            super.loadImage(employeeData.getId(), employeeData.getProfileUrl(), employeeRowController);
            tableContent.getChildren().add(employeeRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding employee row: " + e.getMessage(), e);
        }
    }
}
