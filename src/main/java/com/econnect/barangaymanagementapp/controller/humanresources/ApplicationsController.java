package com.econnect.barangaymanagementapp.controller.humanresources;

import com.econnect.barangaymanagementapp.controller.humanresources.table.application.ApplicationTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
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

import static com.econnect.barangaymanagementapp.enumeration.path.fxmlPath.EMPLOYEE_APPLICATION_TABLE;

public class ApplicationsController {
    @FXML
    private VBox content;

    @FXML
    private TextField searchField;

    private final EmployeeService employeeService;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private ApplicationTableController applicationTableController;
    private final DependencyInjector dependencyInjector;

    private List<Employee> allPendingEmployees;
    private Task<List<Employee>> searchTask;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public ApplicationsController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.employeeService = dependencyInjector.getEmployeeService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    public void initialize() {
        loadApplicationTable();
        populateEmployeeRows();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performSearch());
            searchDelay.playFromStart();
        });
    }

    private void loadApplicationTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(EMPLOYEE_APPLICATION_TABLE.getFxmlPath(), dependencyInjector, this);
            Parent employeeTable = loader.load();
            applicationTableController = loader.getController();
            content.getChildren().add(employeeTable);
        } catch (IOException e) {
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    public void populateEmployeeRows() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(content.getWidth(), content.getHeight());
        Platform.runLater(() -> content.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            allPendingEmployees = employeeService.findAllApplicants();

            Platform.runLater(() -> {
                content.getChildren().remove(loadingIndicator);
                if (allPendingEmployees.isEmpty()) {
                    applicationTableController.clearTable();
                    applicationTableController.showNoData();
                } else {
                    updateEmployeeTable(allPendingEmployees);
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
                return allPendingEmployees.stream()
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
                || employee.getStatus().getName().toLowerCase().contains(searchText)
                || employee.getApplicationType().getName().toLowerCase().contains(searchText)
                || employee.getCreatedAt().toString().contains(searchText)
                || DateFormatter.extractDateAndFormat(employee.getCreatedAt()).toLowerCase().contains(searchText)
                || DateFormatter.extractTimeAndFormat(employee.getCreatedAt()).toLowerCase().contains(searchText);
    }

    private void updateEmployeeTable(List<Employee> employees) {
        applicationTableController.clearTable();

        if (employees.isEmpty()) {
            applicationTableController.showNoData();
        } else {
            employees.forEach(employee -> {
                applicationTableController.addEmployeeRow(
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
}
