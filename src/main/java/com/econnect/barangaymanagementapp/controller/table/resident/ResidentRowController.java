package com.econnect.barangaymanagementapp.controller.table.resident;

import com.econnect.barangaymanagementapp.controller.base.BaseRowController;
import com.econnect.barangaymanagementapp.controller.detail.ViewResidentController;
import com.econnect.barangaymanagementapp.controller.form.EditResidentController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
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

public class ResidentRowController extends BaseRowController<Resident> {
    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private final ResidentService residentService;
    private final DependencyInjector dependencyInjector;
    private List<RolePermission.Action> allowedActions;
    private Employee loggedEmployee;
    @Getter
    private String residentId;

    @FXML
    private HBox tableRow, buttonContainer;

    @FXML
    private Label residentIdLabel, lastNameLabel, firstNameLabel, statusLabel, contactNumberLabel, emailLabel, dateLabel, timeLabel;

    @FXML
    private ImageView profilePicture;

    public ResidentRowController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.dependencyInjector = dependencyInjector;
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
        this.residentService = dependencyInjector.getResidentService();
        this.loggedEmployee = UserSession.getInstance().getCurrentEmployee();
    }

    public void initialize() {
        setupProfileImageClick();
        setupRowClickEvents();
        allowedActions = RolePermission.getActionByRole(RolePermission.TableActions.RESIDENT, loggedEmployee.getRole());
    }

    @Override
    protected void setData(Resident residentData) {
        this.residentId = residentData.getId();
        Platform.runLater(() -> setupButtonContainer());

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
            case VERIFIED:
                createEditButton();
                createSuspendButton();
                break;
            case SUSPENDED:
                createRestoreButton();
                createDeleteButton();
                break;
            default:
                addInvisibleButtons(2);
                break;
        }
    }

    private void createSuspendButton() {
        Button suspend = ButtonUtils.createButton("Suspend", ButtonStyle.WARNING, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Suspend", "Would you like to suspend resident #" + residentIdLabel.getText() + "?", isConfirmed -> {
                if (isConfirmed) updateResidentStatus(SUSPENDED);
            });
        });
        setButtonState(suspend, allowedActions, RolePermission.Action.SUSPEND);
        buttonContainer.getChildren().add(suspend);
    }

    private void createDeleteButton() {
        Button delete = ButtonUtils.createButton("Delete", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Delete", "Would you like to delete resident #" + residentIdLabel.getText() + "?", isConfirmed -> {
                if (isConfirmed) updateResidentStatus(REMOVED);
            });
        });
        setButtonState(delete, allowedActions, RolePermission.Action.DELETE);
        buttonContainer.getChildren().add(delete);
    }

    private void createRestoreButton() {
        Button restore = ButtonUtils.createButton("Restore", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Restore", "Would you like to restore resident #" + residentIdLabel.getText() + "?", isConfirmed -> {
                if (isConfirmed) updateResidentStatus(VERIFIED);
            });
        });
        setButtonState(restore, allowedActions, RolePermission.Action.RESTORE);
        buttonContainer.getChildren().add(restore);
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

    private void createEditButton() {
        Button edit = ButtonUtils.createButton("Edit", ButtonStyle.UPDATE, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.EDIT_RESIDENT,
                    EditResidentController.class,
                    controller -> controller.setId(residentIdLabel.getText())
            );
        });
        setButtonState(edit, allowedActions, RolePermission.Action.EDIT);
        buttonContainer.getChildren().add(edit);
    }

    private void addInvisibleButtons(int count) {
        for (int i = 0; i < count; i++) {
            buttonContainer.getChildren().add(ButtonUtils.createInvisibleButton());
        }
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

                Response response = getValue();
                if (response.isSuccessful()) {
                    if (status == SUSPENDED) {
                        modalUtils.showModal(Modal.SUCCESS, "Suspended", "Resident #" + residentIdLabel.getText() + " has been suspended.");
                    } else {
                        modalUtils.showModal(Modal.SUCCESS, "Restored", "Resident #" + residentIdLabel.getText() + " has been restored.");
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