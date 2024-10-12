package com.econnect.barangaymanagementapp.controller.humanresources.table.application;

import com.econnect.barangaymanagementapp.controller.humanresources.ApplicationsController;
import com.econnect.barangaymanagementapp.controller.humanresources.modal.SetupAccountController;
import com.econnect.barangaymanagementapp.controller.humanresources.modal.ViewEmployeeApplicationController;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.enumeration.ui.CustomizeModal;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.ButtonUtils;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import okhttp3.Response;

public class ApplicationRowController {
    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private final EmployeeService employeeService;
    private final ApplicationsController applicationsController;
    private final DependencyInjector dependencyInjector;
    private final UserSession userSession;

    @FXML
    private HBox tableRow, buttonContainer;


    @FXML
    private Label residentIdLabel, lastNameLabel, firstNameLabel, statusLabel, typeLabel, dateLabel, timeLabel;

    @FXML
    private ImageView profilePicture;

    public ApplicationRowController(DependencyInjector dependencyInjector, ApplicationsController applicationsController) {
        this.dependencyInjector = dependencyInjector;
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.applicationsController = applicationsController;
        this.userSession = UserSession.getInstance();
    }

    public void initialize() {
        setupProfileImageClick();
        setupRowClickEvents();
        setupButtonContainer();
    }

    public void setEmployeeData(String employeeId, String lastName, String firstName, String status, String type, String date, String time, Image profileImage) {
        residentIdLabel.setText(employeeId);
        lastNameLabel.setText(lastName);
        firstNameLabel.setText(firstName);
        statusLabel.setText(status);
        typeLabel.setText(type);
        dateLabel.setText(date);
        timeLabel.setText(time);
        profilePicture.setImage(profileImage);
    }

    public void setProfileImage(Image profileImage) {
        profilePicture.setImage(profileImage);
    }

    private void setupProfileImageClick() {
        ImageUtils.setCircleClip(profilePicture);
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), parentStage));
    }

    private void setupRowClickEvents() {
        // Add Selection Style
        tableRow.setOnMouseClicked(_ -> {
            if (tableRow.getStyleClass().contains("selected")) {
                tableRow.getStyleClass().remove("selected");
            } else {
                tableRow.getStyleClass().add("selected");
            }
        });

        // Remove selection style
        tableRow.setOnMouseExited(_ -> {
            if (!tableRow.getStyleClass().contains("selected")) {
                tableRow.setStyle("");
            }
        });
    }

    private void setupButtonContainer() {
        Button viewBtn = ButtonUtils.createButton("View", ButtonStyle.VIEW, () -> {
            modalUtils.customizeModalWithCallback(
                    CustomizeModal.VIEW_APPLICATION_EMPLOYEE,
                    ViewEmployeeApplicationController.class,
                    controller -> controller.setId(residentIdLabel.getText())
            );
        });

        Button acceptBtn = ButtonUtils.createButton("Accept", ButtonStyle.ACCEPT, () -> {
            modalUtils.customizeModalWithCallback(CustomizeModal.SETUP_ACCOUNT, SetupAccountController.class,
                    controller -> controller.setId(residentIdLabel.getText()), dependencyInjector, applicationsController);
        });

        Button rejectBtn = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Reject", "Are you sure you want to reject this employee application?", isConfirmed -> {
                if (isConfirmed) rejectEmployeeApplication();
            });
        });
        buttonContainer.getChildren().addAll(viewBtn, acceptBtn, rejectBtn);
    }

    private void rejectEmployeeApplication() {
        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                return employeeService.rejectEmployee(residentIdLabel.getText());
            }

            @Override
            protected void succeeded() {
                Response response = getValue();
                if (response.isSuccessful()) {
                    reloadTable();
                    modalUtils.showModal(Modal.SUCCESS, "Rejected", "Employee application has been rejected.");
                } else {
                    modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while evaluating employee application.");
                }
            }
        };
        new Thread(task).start();
    }

    private void reloadTable() {
        applicationsController.populateEmployeeRows();
    }
}