package com.econnect.barangaymanagementapp.controller;

import com.econnect.barangaymanagementapp.controller.base.BaseViewController;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.Validator;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import okhttp3.Response;

import java.util.List;
import java.util.stream.Collectors;

public class EditAccountController implements BaseViewController {
    @FXML
    private AnchorPane rootContainer;
    @FXML
    private Button confirmBtn, cancelBtn;
    @FXML
    private ImageView closeBtn;
    @FXML
    private ComboBox<String> departmentComboBox, roleComboBox;

    private final ModalUtils modalUtils;
    private final EmployeeService employeeService;
    private final Validator validator;
    private DepartmentType selectedDepartment;
    private String residentId;


    public EditAccountController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.validator = dependencyInjector.getValidator();
    }

    public void initialize() {
        setupActionButtons();
        populateDepartmentComboBox();
        setupEventListener();
    }

    private void setupActionButtons() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateForm());
    }

    private void validateForm() {
        ComboBox[] comboBoxes = {departmentComboBox, roleComboBox};
        if (validator.hasEmptyComboBox(comboBoxes)) return;
        updateAccount();
    }

    private void updateAccount() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootContainer.getWidth(), rootContainer.getHeight());
        Platform.runLater(() -> rootContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            try {
                Response response = employeeService.updateEmployeeDepartmentAndRole(residentId, selectedDepartment, RoleType.fromName(roleComboBox.getValue()));
                Platform.runLater(() -> {
                    rootContainer.getChildren().remove(loadingIndicator);
                    if (response.isSuccessful()) {
                        closeWindow();
                        modalUtils.showModal(Modal.SUCCESS, "Success", "Employee #" + residentId + " has been successfully updated.");
                    } else {
                        closeWindow();
                        modalUtils.showModal(Modal.ERROR, "Failed", "An error occurred while updating employee.");
                    }
                });
            } catch (Exception e) {
                modalUtils.showModal(Modal.ERROR, "Error", "An exception occurred while updating employee.");
                e.printStackTrace();
                Platform.runLater(() -> rootContainer.getChildren().remove(loadingIndicator));
            }
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> rootContainer.getChildren().remove(loadingIndicator));
            System.err.println("Failed to activate employee");
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

    private void setupEventListener() {
        List<ComboBox<String>> comboBoxes = List.of(departmentComboBox, roleComboBox);
        departmentComboBox.setOnAction(_ -> {
            String selectedDepartmentName = departmentComboBox.getValue();
            selectedDepartment = getDepartmentByName(selectedDepartmentName);
            if (selectedDepartment != null) {
                populateRoleComboBox(selectedDepartment);
            }
        });
        validator.setupComboBox(comboBoxes);

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

    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();
        });
    }

    @Override
    public void setId(String id) {
        this.residentId = id;
    }
}
