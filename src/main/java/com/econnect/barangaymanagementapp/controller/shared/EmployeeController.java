package com.econnect.barangaymanagementapp.controller.shared;

import com.econnect.barangaymanagementapp.controller.shared.table.employee.EmployeeTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.service.SearchService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
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

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.EMPLOYEE_TABLE;

public class EmployeeController {

    @FXML
    private VBox contentPane;

    @FXML
    private TextField searchField;

    private final EmployeeService employeeService;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private EmployeeTableController employeeTableController;
    private final SearchService<Employee> searchService;
    private final DependencyInjector dependencyInjector;

    private List<Employee> allEmployees;
    private StackPane loadingIndicator;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public EmployeeController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.employeeService = dependencyInjector.getEmployeeService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.searchService = dependencyInjector.getEmployeeSearchService();
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
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(EMPLOYEE_TABLE.getFxmlPath(), dependencyInjector, this);
            Parent employeeTable = loader.load();
            employeeTableController = loader.getController();
            contentPane.getChildren().add(employeeTable);
        } catch (IOException e) {
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    public void populateEmployeeRows() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(contentPane.getWidth(), contentPane.getHeight());
        Platform.runLater(() -> contentPane.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            allEmployees = employeeService.findAllActiveEmployees();
            Platform.runLater(() -> {
                contentPane.getChildren().remove(loadingIndicator);
                if (allEmployees.isEmpty()) {
                    employeeTableController.clearRow();
                    employeeTableController.showNoData();
                } else {
                    updateEmployeeTable(allEmployees);
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
                allEmployees,
                searchService.createEmployeeFilter(searchText),
                (filteredEmployees) -> updateEmployeeTable(filteredEmployees));
    }

    public void addLoadingIndicator() {
        loadingIndicator = LoadingIndicator.createLoadingIndicator(contentPane.getWidth(), contentPane.getHeight());
        contentPane.getChildren().add(loadingIndicator);
    }

    public void removeLoadingIndicator() {
        contentPane.getChildren().remove(loadingIndicator);
    }

    public void reloadTable() {
        populateEmployeeRows();
    }

    private void updateEmployeeTable(List<Employee> employees) {
        employeeTableController.clearRow();

        if (employees.isEmpty()) {
            employeeTableController.showNoData();
            return;
        }
        employees.forEach(employee -> employeeTableController.addRow(employee));
    }

    @FXML
    private void showAddEmployee() {
        modalUtils.customizeModal(FXMLPath.ADD_EMPLOYEE);
    }
}