package com.econnect.barangaymanagementapp.Controller.HumanResources;

import com.econnect.barangaymanagementapp.Controller.HumanResources.Table.Employee.EmployeeTableController;
import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Enumeration.CustomizeModal;
import com.econnect.barangaymanagementapp.Enumeration.Status;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.Utils.LoadingIndicator;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EmployeeController {

    @FXML
    private VBox content;

    @FXML
    private TextField searchField;

    private final EmployeeService employeeService;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private EmployeeTableController employeeTableController;

    private List<Employee> allEmployees;
    private Task<List<Employee>> searchTask;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public EmployeeController(DependencyInjector dependencyInjector) {
        this.employeeService = dependencyInjector.getEmployeeService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    public void initialize() {
        loadEmployeeTable();
        populateEmployeeRows();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performSearch());
            searchDelay.playFromStart();
        });
    }

    private void loadEmployeeTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader("View/HumanResources/Table/Employee/employee-table.fxml");
            Parent employeeTable = loader.load();
            employeeTableController = loader.getController();
            content.getChildren().add(employeeTable);
        } catch (IOException e) {
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    public void populateEmployeeRows() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(content.getWidth(), content.getHeight());
        Platform.runLater(() -> content.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            allEmployees = employeeService.findAllEmployeesStatusExcept(Status.EmployeeStatus.PENDING);

            Platform.runLater(() -> {
                content.getChildren().remove(loadingIndicator);
                if (allEmployees.isEmpty()) {
                    employeeTableController.showNoData();
                } else {
                    updateEmployeeTable(allEmployees);
                }
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> content.getChildren().remove(loadingIndicator));
            System.err.println("Error loading employees");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }


    private void performSearch() {
        if (searchTask != null && searchTask.isRunning()) {
            searchTask.cancel();
        }

        searchTask = new Task<>() {
            @Override
            protected List<Employee> call() {
                String searchText = searchField.getText().trim().toLowerCase();
                return allEmployees.stream()
                        .filter(handleFilter(searchText))
                        .collect(Collectors.toList());
            }

            @Override
            protected void succeeded() {
                List<Employee> filteredEmployees = getValue();
                updateEmployeeTable(filteredEmployees);
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                System.err.println("Error filtering employees: " + exception.getMessage());
            }
        };

        new Thread(searchTask).start();
    }

    private Predicate<Employee> handleFilter(String searchText) {
        return employee -> employee.getId().toLowerCase().contains(searchText)
                || employee.getFirstName().toLowerCase().contains(searchText)
                || employee.getLastName().toLowerCase().contains(searchText)
                || employee.getRole().getName().toLowerCase().contains(searchText)
                || employee.getStatus().getName().toLowerCase().contains(searchText)
                || employee.getDepartment().getName().toLowerCase().contains(searchText);
    }

    private void updateEmployeeTable(List<Employee> employees) {
        employeeTableController.clearTable();

        if (employees.isEmpty()) {
            employeeTableController.showNoData();
        } else {
            employees.forEach(employee -> {
                employeeTableController.addEmployeeRow(
                        employee.getId(),
                        employee.getLastName(),
                        employee.getFirstName(),
                        employee.getRole(),
                        employee.getDepartment(),
                        employee.getStatus(),
                        employee.getProfileUrl()
                );
            });
        }
    }

    @FXML
    private void showAddEmployee() {
        modalUtils.customizeModal(CustomizeModal.ADD_EMPLOYEE);
    }
}