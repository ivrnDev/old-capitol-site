package com.econnect.barangaymanagementapp.controller.barangayoffice.table.request;

import com.econnect.barangaymanagementapp.controller.shared.base.BaseRowController;
import com.econnect.barangaymanagementapp.controller.shared.modal.ViewResidentController;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.ButtonUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;
import okhttp3.Response;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus.*;

public class RequestRowController extends BaseRowController<Request> {
    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private final ResidentService residentService;
    private final DependencyInjector dependencyInjector;
    @Getter
    private String requestId;

    @FXML
    private HBox tableRow, buttonContainer;

    @FXML
    private Label requestIdLabel, residentIdLabel, requestTypeLabel, statusLabel, dateLabel, timeLabel;

    public RequestRowController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.dependencyInjector = dependencyInjector;
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
        this.residentService = dependencyInjector.getResidentService();
    }

    public void initialize() {
        setupRowClickEvents();
    }

    @Override
    protected void setData(Request request) {
        this.requestId = request.getId();
//        Platform.runLater(() -> setupButtonContainer());
        requestIdLabel.setText(request.getReferenceNumber());
        residentIdLabel.setText(request.getResidentId());
        requestTypeLabel.setText(request.getRequestType() != null ? request.getRequestType().getName() : "");
        statusLabel.setText(request.getStatus().getName());
        dateLabel.setText(DateFormatter.formatDateToLongStyle(request.getCreatedAt()));
        timeLabel.setText(DateFormatter.formatTimeTo12HourStyle(request.getCreatedAt()));
    }

    @Override
    public void setImage(Image profileImage) {
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
                setupActiveButton();
                setupDeleteButton();
                break;
            case SUSPENDED:
                setupInactiveButton();
                setupDeleteButton();
                break;
            default:
                setupDeleteButton();
                buttonContainer.getChildren().add(ButtonUtils.createInvisibleButton());
                break;
        }
    }

    private void setupInactiveButton() {
        Button restore = ButtonUtils.createButton("Restore", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Restore", "Would you like to restore resident #" + residentIdLabel.getText() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(VERIFIED);
            });
        });
        buttonContainer.getChildren().addAll(restore);
    }

    private void setupActiveButton() {
        Button suspend = ButtonUtils.createButton("Suspend", ButtonStyle.WARNING, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Suspend", "Would you like to suspend resident #" + residentIdLabel.getText() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(SUSPENDED);
            });
        });
        buttonContainer.getChildren().addAll(suspend);
    }

    private void setupDeleteButton() {
        Button delete = ButtonUtils.createButton("Delete", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Delete", "Would you like to delete resident #" + residentIdLabel.getText() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(REMOVED);
            });
        });
        buttonContainer.getChildren().add(delete);
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

    private void updateRequestStatus(StatusType.ResidentStatus status) {
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
}