package com.econnect.barangaymanagementapp.controller.barangayoffice.table.resident;

import com.econnect.barangaymanagementapp.controller.barangayoffice.ResidentController;
import com.econnect.barangaymanagementapp.controller.shared.ViewEmployeeApplicationController;
import com.econnect.barangaymanagementapp.controller.shared.base.BaseRowController;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.ButtonUtils;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import okhttp3.Response;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus.*;

public class ResidentApplicationRowController extends BaseRowController<Resident> {
    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private final ResidentService residentService;
    private final ResidentController residentController;

    @FXML
    private HBox tableRow, buttonContainer;

    @FXML
    private Label residentIdLabel, lastNameLabel, firstNameLabel, statusLabel, contactNumberLabel, emailLabel, dateLabel, timeLabel;

    @FXML
    private ImageView profilePicture;

    public ResidentApplicationRowController(DependencyInjector dependencyInjector, ResidentController residentController) {
        super(dependencyInjector);
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
        this.residentService = dependencyInjector.getResidentService();
        this.residentController = residentController;
    }

    public void initialize() {
        setupProfileImageClick();
        setupRowClickEvents();
        Platform.runLater(() -> setupButtonContainer());
    }

    @Override
    protected void setData(Resident residentData) {
        residentIdLabel.setText(residentData.getId());
        lastNameLabel.setText(residentData.getLastName());
        firstNameLabel.setText(residentData.getFirstName());
        statusLabel.setText(residentData.getStatus().getName());
        contactNumberLabel.setText(residentData.getMobileNumber());
        emailLabel.setText(residentData.getEmail());
        dateLabel.setText(DateFormatter.extractDateAndFormat(residentData.getCreatedAt()));
        timeLabel.setText(DateFormatter.extractTimeAndFormat(residentData.getCreatedAt()));
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
        switch (fromName(currentStatus)) {
            case PENDING:
                setupPendingButtons();
                break;
        }
    }

    private void setupPendingButtons() {
        Button verify = ButtonUtils.createButton("Verify", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Verify", "Would you like to verify resident #" + residentIdLabel.getText() + "application?", isConfirmed -> {
                if (isConfirmed) updateResidentStatus(VERIFIED);
            });
        });

        Button reject = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Reject", "Would you like to reject resident #" + residentIdLabel.getText() + "application?", isConfirmed -> {
                if (isConfirmed) updateResidentStatus(REJECTED);
            });
        });

        buttonContainer.getChildren().addAll(verify, reject);
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

    private void updateResidentStatus(StatusType.ResidentStatus status) {
        residentController.addResidentApplicationLoadingIndicator();
        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                return residentService.updateResidentByStatus(residentIdLabel.getText(), status);
            }

            @Override
            protected void succeeded() {
                Response response = getValue();
                if (response.isSuccessful()) {
                    residentController.removeResidentApplicationLoadingIndicator();
                    reloadTable();
                    if (status == VERIFIED) {
                        modalUtils.showModal(Modal.SUCCESS, "Verified", "Resident #" + residentIdLabel.getText() + "appplication has been verified successfully.");
                    } else {
                        modalUtils.showModal(Modal.SUCCESS, "Rejected", "Resident #" + residentIdLabel.getText() + "application has been rejected.");
                    }
                } else {
                    modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while evaluating employee application.");
                }
            }
        };
        new Thread(task).start();
    }

    protected void reloadTable() {
        residentController.reloadResidentApplicationTable();
    }
}