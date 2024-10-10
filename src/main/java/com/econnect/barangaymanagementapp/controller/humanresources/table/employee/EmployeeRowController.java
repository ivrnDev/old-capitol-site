package com.econnect.barangaymanagementapp.controller.humanresources.table.employee;

import com.econnect.barangaymanagementapp.controller.humanresources.EmployeeController;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.enumeration.ui.CustomizeModal;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.ButtonUtils;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import okhttp3.Response;

public class EmployeeRowController {
    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private final EmployeeController employeeController;
    private final EmployeeService employeeService;

    @FXML
    private Label employeeIdLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label positionLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView profilePicture;

    @FXML
    private HBox tableRow;

    @FXML
    private HBox buttonContainer;

    public EmployeeRowController(DependencyInjector dependencyInjector, EmployeeController employeeController) {
        this.employeeController = employeeController;
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
        this.employeeService = dependencyInjector.getEmployeeService();
    }

    public void initialize() {
        setupProfileImageClick();
        setupRowClickEvents();
        setupButtonContainer();
    }

    private void setupProfileImageClick() {
        ImageUtils.setCircleClip(profilePicture);
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), parentStage));
    }

    public void setEmployeeData(String employeeId, String lastName, String firstName, String position, String department, String status, Image profileImage) {
        employeeIdLabel.setText(employeeId);
        lastNameLabel.setText(lastName);
        firstNameLabel.setText(firstName);
        positionLabel.setText(position);
        departmentLabel.setText(department);
        statusLabel.setText(status);
        profilePicture.setImage(profileImage);
    }

    public void setProfileImage(Image profileImage) {
        profilePicture.setImage(profileImage);
    }

    private void setupRowClickEvents() {
        tableRow.setOnMouseClicked(_ -> toggleRowSelection());
        tableRow.setOnMouseExited(_ -> resetRowStyleIfNotSelected());
    }

    private void toggleRowSelection() {
        if (tableRow.getStyleClass().contains("selected")) {
            tableRow.getStyleClass().remove("selected");
        } else {
            tableRow.getStyleClass().add("selected");
        }
    }

    private void resetRowStyleIfNotSelected() {
        if (!tableRow.getStyleClass().contains("selected")) {
            tableRow.setStyle("");
        }
    }

    private void setupButtonContainer() {
        Button updateBtn = ButtonUtils.createButton("Update", ButtonStyle.UPDATE, () -> {
            System.out.println("Clicked update");
        });

        Button viewBtn = ButtonUtils.createButton("View", ButtonStyle.VIEW, () -> {
            modalUtils.customizeModal(CustomizeModal.VIEW_APPLICATION_EMPLOYEE);
        });

        Button deleteBtn = ButtonUtils.createButton("Delete", ButtonStyle.REJECT, () -> {
            try {
                Response response = employeeService.updateEmployeeByStatus(employeeIdLabel.getText(), StatusType.EmployeeStatus.TERMINATED);
                if (response.isSuccessful()) {
                    reloadTable();
                    modalUtils.showModal(Modal.SUCCESS, "Terminated", "Employee " + employeeIdLabel.getText() + " has been terminated");
                }
            } catch (Exception e) {
                modalUtils.showModal(Modal.ERROR, "Error", "Failed to terminate employee");
                throw new RuntimeException(e);
            }
        });
        buttonContainer.getChildren().addAll(updateBtn, viewBtn, deleteBtn);
    }

    private void reloadTable() {
        employeeController.populateEmployeeRows();
    }
}