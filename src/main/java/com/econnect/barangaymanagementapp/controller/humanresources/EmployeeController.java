package com.econnect.barangaymanagementapp.controller.humanresources;

import com.econnect.barangaymanagementapp.controller.humanresources.table.employee.EmployeeTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
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

import static com.econnect.barangaymanagementapp.enumeration.path.fxmlPath.HR_EMPLOYEE_TABLE;
import static com.econnect.barangaymanagementapp.enumeration.ui.CustomizeModal.ADD_EMPLOYEE;

public class EmployeeController {

    @FXML
    private VBox content;

    @FXML
    private TextField searchField;

    private final EmployeeService employeeService;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private EmployeeTableController employeeTableController;
    private final DependencyInjector dependencyInjector;

    private List<Employee> allEmployees;
    private Task<List<Employee>> searchTask;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public EmployeeController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
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
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(HR_EMPLOYEE_TABLE.getFxmlPath(), dependencyInjector, this);
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
            allEmployees = employeeService.findAllActiveEmployees();
            Platform.runLater(() -> {
                content.getChildren().remove(loadingIndicator);
                if (allEmployees.isEmpty()) {
                    employeeTableController.clearTable();
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
        modalUtils.customizeModal(ADD_EMPLOYEE);
    }
}