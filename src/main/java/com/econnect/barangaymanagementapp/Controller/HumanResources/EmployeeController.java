package com.econnect.barangaymanagementapp.Controller.HumanResources;

import com.econnect.barangaymanagementapp.Controller.HumanResources.Table.EmployeeTableController;
import com.econnect.barangaymanagementapp.DTO.EmployeeDTO;
import com.econnect.barangaymanagementapp.Enumeration.CustomizeModal;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    @FXML
    private VBox content; // This is the content of the employee.fxml file

    private final EmployeeService employeeService;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private EmployeeTableController employeeTableController;

    public EmployeeController(DependencyInjector dependencyInjector) {
        this.employeeService = dependencyInjector.getEmployeeService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadEmployeeTable();
        populateEmployeeRows();
    }

    private void loadEmployeeTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader("View/HumanResources/Table/employee-table.fxml");
            Parent employeeTable = loader.load();
            employeeTableController = loader.getController();
            content.getChildren().add(employeeTable);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    private void populateEmployeeRows() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<EmployeeDTO> employees = employeeService.getAllEmployees();
                if (employees.isEmpty()) {
                    Platform.runLater(() -> employeeTableController.showNoData());
                } else {
                    employees.forEach(employee -> {
                        Platform.runLater(() -> {
                            employeeTableController.addEmployeeRow(
                                    employee.getId(),
                                    employee.getLastName(),
                                    employee.getFirstName(),
                                    employee.getRole(),
                                    employee.getDepartment(),
                                    employee.getStatus(),
                                    ""
                            );
                        });
                    });
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void addEmployee() {
        modalUtils.customizeModal(CustomizeModal.ADD_EMPLOYEE);
    }

}