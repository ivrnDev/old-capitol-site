package com.econnect.barangaymanagementapp.Controller.HumanResources;

import com.econnect.barangaymanagementapp.Controller.HumanResources.Table.Application.ApplicationTableController;
import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.Utils.LoadingIndicator;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class ApplicationsController {
    @FXML
    private VBox content;

    private final EmployeeService employeeService;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private ApplicationTableController applicationTableController;

    public ApplicationsController(DependencyInjector dependencyInjector) {
        this.employeeService = dependencyInjector.getEmployeeService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    public void initialize() {
//        loadEmployeeTable();
//        populateEmployeeRows();
    }

    private void loadEmployeeTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader("View/HumanResources/Table/application-table.fxml");
            Parent employeeTable = loader.load();
            applicationTableController = loader.getController();
            content.getChildren().add(employeeTable);
        } catch (IOException e) {
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    private void populateEmployeeRows() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(content.getWidth(), content.getHeight());
        VBox.setVgrow(loadingIndicator, Priority.ALWAYS);
        content.getChildren().add(loadingIndicator);

        Task<List<Employee>> task = new Task<>() {
            @Override
            protected List<Employee> call() {
                return employeeService.findAllEmployees();
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false);
                content.getChildren().remove(loadingIndicator);
                List<Employee> employees = getValue();
                if (employees.isEmpty()) {
                    applicationTableController.showNoData();
                } else {
                    employees.forEach(employee -> {
                        applicationTableController.addApplicationRow(
                                employee.getId(),
                                employee.getLastName(),
                                employee.getFirstName(),
                                employee.getStatus(),
                                employee.getApplicationType(),
                                employee.getCreatedAt(),
                                employee.getProfileUrl()
                        );
                    });
                }
            }


            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                content.getChildren().remove(loadingIndicator);
                Throwable exception = getException();
                System.err.println("Failed to fetch employees: " + exception.getMessage());
                Platform.runLater(() -> applicationTableController.showNoData());
            }
        };

        new Thread(task).start();
    }


}
