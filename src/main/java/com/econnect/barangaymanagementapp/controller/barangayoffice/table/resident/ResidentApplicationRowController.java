package com.econnect.barangaymanagementapp.controller.barangayoffice.table.resident;

import com.econnect.barangaymanagementapp.controller.shared.base.BaseRowController;
import com.econnect.barangaymanagementapp.controller.shared.modal.ViewEmployeeApplicationController;
import com.econnect.barangaymanagementapp.controller.shared.modal.ViewResidentController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.RolePermission;
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

import java.util.List;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus.*;
import static com.econnect.barangaymanagementapp.util.RolePermission.Action.REJECT;

public class ResidentApplicationRowController extends BaseRowController<Resident> {
    @FXML
    private HBox tableRow, buttonContainer;
    @FXML
    private Label residentIdLabel, lastNameLabel, firstNameLabel, statusLabel, dateLabel, timeLabel, contactNumberLabel;
    @FXML
    private ImageView profilePicture;

    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private final ResidentService residentService;
    private Employee loggedEmployee;
    @Getter
    private String residentId;

    public ResidentApplicationRowController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
        this.residentService = dependencyInjector.getResidentService();
        this.loggedEmployee = UserSession.getInstance().getCurrentEmployee();
    }

    public void initialize() {
        setupProfileImageClick();
        setupRowClickEvents();
    }

    @Override
    protected void setData(Resident residentData) {
        Platform.runLater(() -> setupButtonContainer());
        residentId = residentData.getId();

        residentIdLabel.setText(residentData.getId());
        lastNameLabel.setText(residentData.getLastName());
        firstNameLabel.setText(residentData.getFirstName());
        statusLabel.setText(residentData.getStatus().getName());
//        contactNumberLabel.setText(residentData.getMobileNumber());
//        emailLabel.setText(residentData.getEmail());
        dateLabel.setText(DateFormatter.formatDateToLongStyle(residentData.getCreatedAt()));
        timeLabel.setText(DateFormatter.formatTimeTo12HourStyle(residentData.getCreatedAt()));
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

        setupViewButton();
        String currentStatus = statusLabel.getText();
        switch (fromName(currentStatus)) {
            case PENDING:
                createVerifyButton();
                createRejectButton();
                break;
        }
    }

    private void createVerifyButton() {
        Button verify = ButtonUtils.createButton("Verify", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Verify", "Would you like to verify resident #" + residentIdLabel.getText() + "application?", isConfirmed -> {
                if (isConfirmed) updateResidentStatus(VERIFIED);
            });
        });
        setButtonState(verify, RolePermission.getActionByRole(RolePermission.TableActions.RESIDENT, loggedEmployee.getRole()), RolePermission.Action.VERIFY);
        buttonContainer.getChildren().add(verify);
    }

    private void createRejectButton() {
        Button reject = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Reject", "Would you like to reject resident #" + residentIdLabel.getText() + "application?", isConfirmed -> {
                if (isConfirmed) updateResidentStatus(REJECTED);
            });
        });
        setButtonState(reject, RolePermission.getActionByRole(RolePermission.TableActions.RESIDENT, loggedEmployee.getRole()), REJECT);
        buttonContainer.getChildren().add(reject);
    }

    private void setupViewButton() {
        Button viewBtn = ButtonUtils.createButton("View", ButtonStyle.VIEW, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.VIEW_RESIDENT,
                    ViewResidentController.class,
                    controller -> controller.setId(residentIdLabel.getText())
            );
        });
        buttonContainer.getChildren().add(viewBtn);
    }

    private void updateResidentStatus(StatusType.ResidentStatus status) {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(tableRow.getWidth(), tableRow.getHeight());
        tableRow.getChildren().forEach(node -> {
            node.setVisible(false);
            node.setManaged(false);
        });
        tableRow.getChildren().add(loadingIndicator);

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                return residentService.updateResidentByStatus(residentIdLabel.getText(), status);
            }

            @Override
            protected void succeeded() {
                tableRow.getChildren().remove(loadingIndicator);
                tableRow.getChildren().forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });
                initialize();
                Response response = getValue();
                if (response.isSuccessful()) {
                    if (status == VERIFIED) {
                        modalUtils.showModal(Modal.SUCCESS, "Verified", "Resident #" + residentIdLabel.getText() + "appplication has been verified successfully.");
                    } else {
                        modalUtils.showModal(Modal.SUCCESS, "Rejected", "Resident #" + residentIdLabel.getText() + "application has been rejected.");
                    }
                } else {
                    modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while evaluating employee application.");
                }
            }

            @Override
            protected void failed() {
                tableRow.getChildren().remove(loadingIndicator);
                tableRow.getChildren().forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });
                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while evaluating employee application.");
            }
        };
        new Thread(task).start();
    }

    private void setButtonState(Button button, List<RolePermission.Action> allowedActions, RolePermission.Action action) {
        if (!allowedActions.contains(action)) {
            button.setDisable(true);
        }
    }
}