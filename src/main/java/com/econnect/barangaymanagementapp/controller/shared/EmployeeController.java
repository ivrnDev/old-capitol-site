package com.econnect.barangaymanagementapp.controller.shared;

import com.econnect.barangaymanagementapp.controller.shared.table.application.ApplicationTableController;
import com.econnect.barangaymanagementapp.controller.shared.table.employee.EmployeeTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.service.SearchService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
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
import java.util.Optional;
import java.util.Set;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.EMPLOYEE_TABLE;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus.*;

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
    private final LiveReloadUtils liveReloadUtils;
    private final DependencyInjector dependencyInjector;
    private List<Employee> allEmployees;


    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public EmployeeController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.employeeService = dependencyInjector.getEmployeeService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.searchService = dependencyInjector.getEmployeeSearchService();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
    }

    public void initialize() {
        resetLiveReload();
        initializeSSEListener();
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

    private void updateEmployeeTable(List<Employee> employees) {
        employeeTableController.clearRow();

        if (employees.isEmpty()) {
            employeeTableController.showNoData();
            return;
        }
        employees.forEach(employee -> employeeTableController.addRow(employee));
    }

    public void updateEmployeeRow(String id) {
        Set<StatusType.EmployeeStatus> EMPLOYEE_STATUS = Set.of(ACTIVE);
        Optional<Employee> updatedEmployee = employeeService.findEmployeeById(id);
        updatedEmployee.ifPresentOrElse(employee -> {
            if (EMPLOYEE_STATUS.contains(employee.getStatus())) {
                employeeTableController.updateRow(employee);
            } else {
                employeeTableController.deleteRow(employee.getId());

            }
        }, () -> employeeTableController.deleteRow(id));
    }

    private void initializeSSEListener() {
        employeeService.enableLiveReload(result -> Platform.runLater(() -> updateEmployeeRow(result)));
    }

    private void resetLiveReload() {
        liveReloadUtils.stopListeningToUpdates();
    }

    @FXML
    private void showAddEmployee() {
        modalUtils.customizeModal(FXMLPath.ADD_EMPLOYEE);
    }
}