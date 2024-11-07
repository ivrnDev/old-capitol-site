package com.econnect.barangaymanagementapp.controller.shared.table.employee;


import com.econnect.barangaymanagementapp.controller.shared.base.BaseRowController;
import com.econnect.barangaymanagementapp.controller.shared.modal.ViewEmployeeApplicationController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.service.EmployeeService;
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

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus.TERMINATED;

public class EmployeeRowController extends BaseRowController<Employee> {
    @FXML
    private HBox tableRow, buttonContainer;
    @FXML
    private Label employeeIdLabel, lastNameLabel, firstNameLabel, positionLabel, departmentLabel, statusLabel;
    @FXML
    private ImageView profilePicture;

    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private final EmployeeService employeeService;
    private final DependencyInjector dependencyInjector;
    private final UserSession userSession;
    @Getter
    private String employeeId;

    public EmployeeRowController(DependencyInjector dependencyInjector) {
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
        this.employeeId = employeeData.getId();

        employeeIdLabel.setText(employeeData.getId());
        lastNameLabel.setText(employeeData.getLastName());
        firstNameLabel.setText(employeeData.getFirstName());
        statusLabel.setText(employeeData.getStatus().getName());
        positionLabel.setText(employeeData.getRole().getName());
        departmentLabel.setText(employeeData.getDepartment().getName());
        statusLabel.setText(employeeData.getStatus().getName());
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
        setupViewButton();
        String currentStatus = statusLabel.getText();
//        if (!fromName(currentStatus).equals(ACTIVE)) {
//            setupInactiveButton();
//            return;
//        }
        setupActiveButton();
    }

    private void setupActiveButton() {
        Button updateBtn = ButtonUtils.createButton("Update", ButtonStyle.UPDATE, () -> {
//            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Notify", "Would you like to send an email to this employee requesting them to submit their pending requirements?", isConfirmed -> {
//                if (isConfirmed) updateEmployeeToUnderReview();
//            });
        });

        Button terminateBtn = ButtonUtils.createButton("Terminate", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Terminate", "Are you sure you want to terminate employee" + employeeIdLabel.getText() + "?", isConfirmed -> {
                if (isConfirmed) updateEmployeeStatus(TERMINATED);
            });
        });
        buttonContainer.getChildren().addAll(updateBtn, terminateBtn);
    }

    private void setupViewButton() {
        Button viewBtn = ButtonUtils.createButton("View", ButtonStyle.VIEW, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.VIEW_APPLICATION_EMPLOYEE,
                    ViewEmployeeApplicationController.class,
                    controller -> controller.setId(employeeIdLabel.getText())
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
//                    case ACTIVE -> response = employeeService.activateEmployee(employeeId);
                    case TERMINATED -> response = employeeService.terminateEmployee(employeeId);
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
                        case TERMINATED ->
                                modalUtils.showModal(Modal.SUCCESS, "Terminated", "Employee + " + employeeId + " has been terminated successfully.");
                    }
                } else {
                    modalUtils.showModal(Modal.ERROR, "Failed", "An error occurred while updating employee.");
                }
            }

            @Override
            protected void failed() {
                tableRow.getChildren().remove(loadingIndicator);
                tableRow.getChildren().forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });
                modalUtils.showModal(Modal.ERROR, "Error", "An exception occurred while updating employee.");
            }
        };
        new Thread(task).start();
    }
}