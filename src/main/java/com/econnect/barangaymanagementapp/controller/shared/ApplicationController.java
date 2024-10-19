package com.econnect.barangaymanagementapp.controller.shared;

import com.econnect.barangaymanagementapp.controller.shared.base.BaseTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.service.SearchService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.EMPLOYEE_APPLICATION_TABLE;

public class ApplicationController<T extends BaseTableController<Employee>> {
    @FXML
    private TextField searchField;

    @FXML
    private VBox contentPane;

    private final EmployeeService employeeService;
    private final SearchService<Employee> searchService;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;
    private T tableController;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private List<Employee> allApplications;
    private StackPane loadingIndicator;

    public ApplicationController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.employeeService = dependencyInjector.getEmployeeService();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.searchService = dependencyInjector.getEmployeeSearchService();
    }

    public void initialize() {
        loadApplicationTable();
        populateApplicationRows();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performSearch());
            searchDelay.playFromStart();
        });
    }

    private void loadApplicationTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(EMPLOYEE_APPLICATION_TABLE.getFxmlPath(), dependencyInjector, this);
            Parent employeeTable = loader.load();
            tableController = loader.getController();
            contentPane.getChildren().add(employeeTable);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    private void populateApplicationRows() {
        addLoadingIndicator();
        Runnable call = () -> {
            allApplications = employeeService.findAllApplicants();

            Platform.runLater(() -> {
                removeLoadingIndicator();
                if (allApplications.isEmpty()) {
                    tableController.clearRow();
                    tableController.showNoData();
                } else {
                    updateEmployeeTable(allApplications);
                }
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> contentPane.getChildren().remove(loadingIndicator));
            System.err.println("Error loading employees");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        searchService.performSearch(
                searchText,
                allApplications,
                searchService.createEmployeeApplicationFilter(searchText),
                (filteredApplications) -> updateEmployeeTable(filteredApplications));
    }

    public void addLoadingIndicator() {
        loadingIndicator = LoadingIndicator.createLoadingIndicator(contentPane.getWidth(), contentPane.getHeight());
        contentPane.getChildren().add(loadingIndicator);
    }

    public void removeLoadingIndicator() {
        contentPane.getChildren().remove(loadingIndicator);
    }

    public void reloadTable() {
        populateApplicationRows();
    }

    private void updateEmployeeTable(List<Employee> employees) {
        tableController.clearRow();

        if (employees.isEmpty()) {
            tableController.showNoData();
        } else {
            employees.forEach(employee -> {
                Employee currentEmployee = Employee.builder().
                        id(employee.getId()).
                        lastName(employee.getLastName()).
                        firstName(employee.getFirstName()).
                        status(employee.getStatus()).
                        applicationType(employee.getApplicationType()).
                        createdAt(employee.getCreatedAt()).
                        profileUrl(employee.getProfileUrl()).
                        build();
                tableController.addRow(currentEmployee);
            });
        }
    }

}
