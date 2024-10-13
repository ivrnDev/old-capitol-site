package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.controller.component.BaseViewController;
import com.econnect.barangaymanagementapp.controller.humanresources.ApplicationsController;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import okhttp3.Response;

import java.util.List;
import java.util.stream.Collectors;

public class SetupAccountController implements BaseViewController {
    private final ModalUtils modalUtils;
    private final EmployeeService employeeService;
    private ApplicationsController applicationsController;
    private DepartmentType selectedDepartment;
    private String employeeId;

    @FXML
    private AnchorPane rootContainer;

    @FXML
    private Button confirmBtn, cancelBtn;

    @FXML
    private ComboBox<String> departmentComboBox, roleComboBox;

    public SetupAccountController(DependencyInjector dependencyInjector, ApplicationsController applicationsController) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.applicationsController = applicationsController;
    }

    public void initialize() {
        setupActionButtons();
        populateDepartmentComboBox();
        setupDepartmentComboBoxListener();
    }

    private void setupActionButtons() {
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateForm());
    }

    private void validateForm() {
        String role = roleComboBox.getValue();
        String department = departmentComboBox.getValue();

        if (role == null || role.isEmpty() || department == null || department.isEmpty()) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Please select values for both fields.");
        } else {
            updateAccount();
        }
    }

    private void updateAccount() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootContainer.getWidth(), rootContainer.getHeight());
        Platform.runLater(() -> rootContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            try {
                Response response = employeeService.activateEmployee(employeeId, selectedDepartment, RoleType.fromName(roleComboBox.getValue()));
                Platform.runLater(() -> {
                    rootContainer.getChildren().remove(loadingIndicator);
                    if (response.isSuccessful()) {
                        applicationsController.populateEmployeeRows();
                        closeWindow();
                        modalUtils.showModal(Modal.SUCCESS, "Success", "Employee + " + employeeId + " has been successfully evaluated.");
                    } else {
                        closeWindow();
                        modalUtils.showModal(Modal.ERROR, "Failed", "An error occurred while activating employee application.");
                    }
                });
            } catch (Exception e) {
                modalUtils.showModal(Modal.ERROR, "Error", "An exception occurred while activating employee application.");
                e.printStackTrace();
                Platform.runLater(() -> rootContainer.getChildren().remove(loadingIndicator));
            }
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> rootContainer.getChildren().remove(loadingIndicator));
            System.err.println("Faled to activate employee");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void populateDepartmentComboBox() {
        List<String> departmentNames = FXCollections.observableArrayList(
                        DepartmentType.values()).stream()
                .filter(department -> department != DepartmentType.NONE)
                .map(DepartmentType::getName)
                .collect(Collectors.toList());

        departmentComboBox.setItems(FXCollections.observableArrayList(departmentNames));
    }

    private void setupDepartmentComboBoxListener() {
        departmentComboBox.setOnAction(_ -> {
            String selectedDepartmentName = departmentComboBox.getValue();
            selectedDepartment = getDepartmentByName(selectedDepartmentName);
            if (selectedDepartment != null) {
                populateRoleComboBox(selectedDepartment);
            }
        });
    }

    private void populateRoleComboBox(DepartmentType departmentType) {
        roleComboBox.getItems().clear();

        List<String> roleNames = departmentType.getRoles().stream()
                .map(RoleType::getName)
                .collect(Collectors.toList());

        roleComboBox.setItems(FXCollections.observableArrayList(roleNames));
    }

    private DepartmentType getDepartmentByName(String name) {
        for (DepartmentType department : DepartmentType.values()) {
            if (department.getName().equals(name)) {
                return department;
            }
        }
        return null;
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }

    @FXML
    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();
        });
    }

    @Override
    public void setId(String id) {
        this.employeeId = id;
    }
}
