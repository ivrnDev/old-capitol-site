package com.econnect.barangaymanagementapp.controller.barangayoffice.table.request;

import com.econnect.barangaymanagementapp.controller.barangayoffice.modal.view.ViewDocumentRequestController;
import com.econnect.barangaymanagementapp.controller.shared.base.BaseRowController;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.BarangayIdStatus;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.CertificateStatus;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.service.BarangayidService;
import com.econnect.barangaymanagementapp.service.CertificateService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.ButtonUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import okhttp3.Response;


public class RequestRowController extends BaseRowController<Request> {
    private final ModalUtils modalUtils;
    private final ResidentService residentService;
    private final CertificateService certificateService;
    private final BarangayidService barangayidService;
    @Getter
    private String requestId;
    private Request request;

    @FXML
    private HBox tableRow, buttonContainer;

    @FXML
    private Label requestIdLabel, residentIdLabel, requestTypeLabel, statusLabel, dateLabel, timeLabel;

    public RequestRowController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.certificateService = dependencyInjector.getCertificateService();
        this.barangayidService = dependencyInjector.getBarangayidService();
    }

    public void initialize() {
        setupRowClickEvents();
    }

    @Override
    protected void setData(Request request) {
        this.request = request;
        this.requestId = request.getId();
        Platform.runLater(() -> setupButtonContainer());
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

    @Override
    public void setupButtonContainer() {
        buttonContainer.getChildren().clear();
        setupViewButton();
        String currentStatus = statusLabel.getText();
        switch (request.getRequestType()) {
            case CERTIFICATES:
                setupDocumentButton(currentStatus);
                break;
            case BARANGAY_ID:
                setupDocumentButton(currentStatus);
                break;
        }
    }

    private void setupDocumentButton(String currentStatus) {
        switch (StatusType.RequestStatus.fromName(currentStatus)) {
            case PENDING:
                createAcceptButton();
                createRejectButton();
                break;
            case IN_PROGRESS:
                createReleaseButton();
                createRejectButton();
                break;
            case RELEASING:
                createCompletedButton();
                createRejectButton();
                break;
            case REJECTED:
                createRestoreButton();
                createDeleteButton();
                break;
            case COMPLETED:
                invisibleButton();
                invisibleButton();
                break;
            default:
                createRejectButton();
                buttonContainer.getChildren().add(ButtonUtils.createInvisibleButton());
                break;
        }
    }

    private void createAcceptButton() {
        String head;
        String message;

        switch (request.getRequestType()) {
            case CERTIFICATES:
                head = "Accept Request";
                message = "Would you like to accept request #" + request.getReferenceNumber() + "?";
                break;
            case BARANGAY_ID:
                head = "Accept Request";
                message = "Would you like to accept request #" + request.getReferenceNumber() + "?";
                break;
            default:
                head = "Accept Request";
                message = "Would you like to accept request #" + request.getReferenceNumber() + "?";
                break;
        }
        Button accept = ButtonUtils.createButton("Accept", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, head, message, isConfirmed -> {
                if (isConfirmed) updateRequestStatus(StatusType.RequestStatus.IN_PROGRESS);
            });
        });

        buttonContainer.getChildren().add(accept);
    }

    private void createReleaseButton() {
        String head;
        String message;

        switch (request.getRequestType()) {
            case CERTIFICATES:
                head = "Release";
                message = "Would you like to release the certificate for request #" + request.getReferenceNumber() + "?";
                break;
            case BARANGAY_ID:
                head = "Release";
                message = "Would you like to release the ID for request #" + request.getReferenceNumber() + "?";
                break;
            default:
                head = "Release";
                message = "Would you like to release request #" + request.getReferenceNumber() + "?";
                break;
        }

        Button release = ButtonUtils.createButton("Release", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, head, message, isConfirmed -> {
                if (isConfirmed) updateRequestStatus(StatusType.RequestStatus.RELEASING);
            });
        });

        buttonContainer.getChildren().add(release);
    }

    private void createCompletedButton() {
        String head;
        String message;

        switch (request.getRequestType()) {
            case CERTIFICATES:
                head = "Mark as complete";
                message = "Would you like to mark request #" + request.getReferenceNumber() + " as completed?";
                break;
            case BARANGAY_ID:
                head = "Mark as complete";
                message = "Would you like to mark request #" + request.getReferenceNumber() + " as completed?";
                break;
            default:
                head = "Mark as complete";
                message = "Would you like to mark request #" + request.getReferenceNumber() + " as completed?";
                break;
        }

        Button accept = ButtonUtils.createButton("Complete", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, head, message, isConfirmed -> {
                if (isConfirmed) updateRequestStatus(StatusType.RequestStatus.COMPLETED);
            });
        });

        buttonContainer.getChildren().add(accept);
    }

    private void createRestoreButton() {
        Button reject = ButtonUtils.createButton("Restore", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Restore", "Would you like to reject request #" + request.getReferenceNumber() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(StatusType.RequestStatus.PENDING);
            });
        });

        buttonContainer.getChildren().add(reject);
    }

    private void createRejectButton() {
        Button reject = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Reject", "Would you like to reject request #" + request.getReferenceNumber() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(StatusType.RequestStatus.REJECTED);
            });
        });

        buttonContainer.getChildren().add(reject);
    }

    private void createDeleteButton() {
        Button delete = ButtonUtils.createButton("Delete", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Delete", "Would you like to delete request #" + request.getReferenceNumber() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(StatusType.RequestStatus.REJECTED);
            });
        });

        buttonContainer.getChildren().add(delete);
    }

    private void invisibleButton() {
        buttonContainer.getChildren().add(ButtonUtils.createInvisibleButton());
    }

    private void setupViewButton() {
        Button viewBtn = ButtonUtils.createButton("Details", ButtonStyle.VIEW, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.VIEW_REQUEST,
                    ViewDocumentRequestController.class,
                    controller -> controller.setId(requestId)
            );
        });
        buttonContainer.getChildren().add(viewBtn);
    }


    private void updateRequestStatus(StatusType.RequestStatus status) {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(tableRow.getWidth(), tableRow.getHeight());

        tableRow.getChildren().forEach(node -> {
            node.setVisible(false);
            node.setManaged(false);
        });
        tableRow.getChildren().add(loadingIndicator);

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                switch (request.getRequestType()) {
                    case CERTIFICATES:
                        return certificateService.updateCertificateByStatus(requestId, CertificateStatus.fromName(status.getName()));
                    case BARANGAY_ID:
                        return barangayidService.updateBarangayIdByStatus(requestId, BarangayIdStatus.fromName(status.getName()));
                    default:
                }
                return null;
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
                    modalUtils.showModal(Modal.SUCCESS, "Success", "Status has been updated successfully.");
                } else {
                    modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while updating the status.");
                }
            }

            @Override
            protected void failed() {
                tableRow.getChildren().remove(loadingIndicator);
                tableRow.getChildren().forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });
                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while updating the status.");
            }
        };
        new Thread(task).start();
    }
}