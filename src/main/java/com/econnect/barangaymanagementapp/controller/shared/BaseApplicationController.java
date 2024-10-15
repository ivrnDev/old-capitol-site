package com.econnect.barangaymanagementapp.controller.shared;

import com.econnect.barangaymanagementapp.controller.barangayoffice.table.application.ApplicationTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
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

public abstract class BaseApplicationController {
    @FXML
    private TextField searchField;

    @FXML
    private VBox contentPane;

    private final EmployeeService employeeService;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;
    private ApplicationTableController applicationTableController;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private List<Employee> allPendingEmployees;
    private Task<List<Employee>> searchTask;
    private StackPane loadingIndicator;

    protected abstract FXMLPath applicationTableFXMLPath();

    protected abstract Object currentControllerInstance();

    public BaseApplicationController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.employeeService = dependencyInjector.getEmployeeService();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    public void initialize() {
        loadApplicationTable(applicationTableFXMLPath(), currentControllerInstance());
        populateApplicationRows();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performSearch());
            searchDelay.playFromStart();
        });
    }

    private void loadApplicationTable(FXMLPath fxmlPath, Object controllerClass) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(fxmlPath.getFxmlPath(), dependencyInjector, controllerClass);
            Parent employeeTable = loader.load();
            applicationTableController = loader.getController();
            contentPane.getChildren().add(employeeTable);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    public void populateApplicationRows() {
        addLoadingIndicator();
        Runnable call = () -> {
            allPendingEmployees = employeeService.findAllApplicants();

            Platform.runLater(() -> {
                removeLoadingIndicator();
                if (allPendingEmployees.isEmpty()) {
                    applicationTableController.clearTable();
                    applicationTableController.showNoData();
                } else {
                    updateEmployeeTable(allPendingEmployees);
                }
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> contentPane.getChildren().remove(loadingIndicator));
            System.err.println("Error loading employees");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    public void addLoadingIndicator() {
        loadingIndicator = LoadingIndicator.createLoadingIndicator(contentPane.getWidth(), contentPane.getHeight());
        contentPane.getChildren().add(loadingIndicator);
    }

    public void removeLoadingIndicator() {
        contentPane.getChildren().remove(loadingIndicator);
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
