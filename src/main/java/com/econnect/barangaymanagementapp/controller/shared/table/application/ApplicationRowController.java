package com.econnect.barangaymanagementapp.controller.shared.table.application;

import com.econnect.barangaymanagementapp.controller.shared.ApplicationController;
import com.econnect.barangaymanagementapp.controller.shared.SetupAccountController;
import com.econnect.barangaymanagementapp.controller.shared.SetupRequirementsController;
import com.econnect.barangaymanagementapp.controller.shared.ViewEmployeeApplicationController;
import com.econnect.barangaymanagementapp.controller.shared.base.BaseRowController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.ButtonUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;
import okhttp3.Response;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus.*;

public class ApplicationRowController extends BaseRowController<Employee> {
    @FXML
    private HBox tableRow, buttonContainer;
    @FXML
    private Label residentIdLabel, lastNameLabel, firstNameLabel, statusLabel, typeLabel, dateLabel, timeLabel;
    @FXML
    private ImageView profilePicture;

    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private final EmployeeService employeeService;
    //    private final ApplicationController applicationController;
    private final DependencyInjector dependencyInjector;
    private final UserSession userSession;
    @Getter
    private String residentId;

    public ApplicationRowController(DependencyInjector dependencyInjector, ApplicationController applicationController) {
        super(dependencyInjector);
        this.dependencyInjector = dependencyInjector;
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.userSession = UserSession.getInstance();
    }

    public void initialize() {
        setupProfileImageClick();
        setupRowClickEvents();
    }

    @Override
    protected void setData(Employee employeeData) {
        Platform.runLater(() -> setupButtonContainer());
        residentId = employeeData.getId();
        residentIdLabel.setText(employeeData.getId());
        lastNameLabel.setText(employeeData.getLastName());
        firstNameLabel.setText(employeeData.getFirstName());
        statusLabel.setText(employeeData.getStatus().getName());
        typeLabel.setText(employeeData.getApplicationType().getName());
        dateLabel.setText(DateFormatter.extractDateAndFormat(employeeData.getCreatedAt()));
        timeLabel.setText(DateFormatter.extractTimeAndFormat(employeeData.getCreatedAt()));
    }

    @Override
    public void setImage(Image profileImage) {
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

    protected void setupButtonContainer() {
        buttonContainer.getChildren().clear();
        String currentStatus = statusLabel.getText();
        setupViewButton();
        switch (fromName(currentStatus)) {
            case PENDING:
                setupPendingButtons();
                break;
            case UNDER_REVIEW:
                setupUnderReviewButtons();
                break;
            case EVALUATION:
                setupEvaluationButtons();
                break;
        }
        setupRejectButton();

    }

    private void setupPendingButtons() {
        Button acceptBtn = ButtonUtils.createButton("Notify", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Notify", "Would you like to send an email to this employee requesting them to submit their pending requirements?", isConfirmed -> {
                if (isConfirmed) updateEmployeeStatus(UNDER_REVIEW);
            });
        });
        buttonContainer.getChildren().addAll(acceptBtn);
    }

    private void setupUnderReviewButtons() {
        Button acceptBtn = ButtonUtils.createButton("Evaluate", ButtonStyle.ACCEPT, () -> {
            if (typeLabel.getText().equalsIgnoreCase(ApplicationType.ONLINE.getName())) {
                modalUtils.customizeModalWithCallback(
                        FXMLPath.SETUP_REQUIREMENTS,
                        SetupRequirementsController.class,
                        controller -> controller.setId(residentIdLabel.getText()),
                        dependencyInjector
                );
            } else {
                modalUtils.showModal(Modal.DEFAULT_APPROVE, "Evaluate", "Would you like to send an email to this employee requesting them to submit their pending requirements?", isConfirmed -> {
                    if (isConfirmed) updateEmployeeStatus(EVALUATION);
                });
            }
        });
        buttonContainer.getChildren().add(acceptBtn);
    }

    private void setupRejectButton() {
        Button rejectBtn = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Reject", "Are you sure you want to reject this employee?", isConfirmed -> {
                if (isConfirmed) updateEmployeeStatus(REJECTED);
            });
        });
        buttonContainer.getChildren().add(rejectBtn);
    }

    private void setupEvaluationButtons() {
        Button acceptBtn = ButtonUtils.createButton("Hire", ButtonStyle.ACCEPT, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.SETUP_ACCOUNT,
                    SetupAccountController.class,
                    controller -> controller.setId(residentIdLabel.getText()),
                    dependencyInjector
            );
        });
        buttonContainer.getChildren().addAll(acceptBtn);
    }

    private void setupViewButton() {
        Button viewBtn = ButtonUtils.createButton("View", ButtonStyle.VIEW, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.VIEW_APPLICATION_EMPLOYEE,
                    ViewEmployeeApplicationController.class,
                    controller -> controller.setId(residentIdLabel.getText())
            );
        });
        buttonContainer.getChildren().add(viewBtn);
    }

    private void updateEmployeeStatus(StatusType.EmployeeStatus status) {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(tableRow.getWidth(), tableRow.getHeight());
        // Add loading indicator on the row
        tableRow.getChildren().forEach(node -> {
            node.setVisible(false);
            node.setManaged(false);
        });
        tableRow.getChildren().add(loadingIndicator);

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                Response response = null;
                switch (status) {
                    case UNDER_REVIEW -> response = employeeService.updateEmployeeToUnderReview(residentId);
                    case EVALUATION -> response = employeeService.updateEmployeeToEvaluation(residentId);
                    case REJECTED -> response = employeeService.rejectEmployee(residentId);
                }
                return response;
            }

            @Override
            protected void succeeded() {
                tableRow.getChildren().remove(loadingIndicator);
                tableRow.getChildren().forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });
                Response response = getValue();
                if (response == null) {
                    modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while updating employee application.");
                    return;
                }
                if (response.isSuccessful()) {
                    switch (status) {
                        case UNDER_REVIEW ->
                                modalUtils.showModal(Modal.SUCCESS, "Notified", "Employee + " + residentId + " has been notified successfully.");
                        case EVALUATION ->
                                modalUtils.showModal(Modal.SUCCESS, "Evaluated", "Employee + " + residentId + " has been successully evaluated.");
                        case REJECTED ->
                                modalUtils.showModal(Modal.SUCCESS, "Rejected", "Employee + " + residentId + " has been rejected successfully.");
                    }
                } else {
                    modalUtils.showModal(Modal.ERROR, "Failed", "An error occurred while updating employee application.");
                }
            }

            @Override
            protected void failed() {
                tableRow.getChildren().remove(loadingIndicator);
                tableRow.getChildren().forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });
                modalUtils.showModal(Modal.ERROR, "Error", "An exception occurred while updating employee application.");
            }
        };
        new Thread(task).start();
    }
}