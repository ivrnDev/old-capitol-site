package com.econnect.barangaymanagementapp.controller.table.request;

import com.econnect.barangaymanagementapp.controller.base.BaseRowController;
import com.econnect.barangaymanagementapp.controller.detail.*;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.BarangayIdStatus;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.CertificateStatus;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.service.BarangayidService;
import com.econnect.barangaymanagementapp.service.CedulaService;
import com.econnect.barangaymanagementapp.service.CertificateService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.RolePermission;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import okhttp3.Response;

import java.util.List;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.CedulaStatus;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.RequestStatus;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.RequestStatus.COMPLETED;


public class RequestRowController extends BaseRowController<Request> {
    @FXML
    private HBox tableRow, buttonContainer;
    @FXML
    private Label requestIdLabel, residentIdLabel, requestTypeLabel, statusLabel, applicationTypeLabel, timeLabel;

    private final ModalUtils modalUtils;
    private final CertificateService certificateService;
    private final BarangayidService barangayidService;
    private final CedulaService cedulaService;
    private RoleType roleType;
    private List<RolePermission.Action> allowedActions;
    @Getter
    private String requestId;
    @Getter
    private Request request;

    public RequestRowController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.modalUtils = dependencyInjector.getModalUtils();
        this.certificateService = dependencyInjector.getCertificateService();
        this.barangayidService = dependencyInjector.getBarangayidService();
        this.cedulaService = dependencyInjector.getCedulaService();
        this.roleType = UserSession.getInstance().getEmployeeRole();
    }

    public void initialize() {
        setupRowClickEvents();
        allowedActions = RolePermission.getActionByRole(
                RolePermission.TableActions.REQUEST,
                roleType
        );
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
        applicationTypeLabel.setText(request.getApplicationType().getName());
        timeLabel.setText(DateFormatter.formatToDateTime(request.getCreatedAt()));
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
        String currentStatus = statusLabel.getText();
        buttonContainer.getChildren().clear();
        setupViewButton(request.getRequestType(), currentStatus);
        switch (request.getRequestType()) {
            case CERTIFICATES:
                setupCertificateButton(currentStatus);
                break;
            case BARANGAY_ID:
                setupBarangayIdButton(currentStatus);
                break;
            case CEDULA:
                setupCedulaButton(currentStatus);
                break;
            default:
                addInvisibleButtons(2);
                break;
        }
    }

    private void setupCertificateButton(String currentStatus) {
        switch (RequestStatus.fromName(currentStatus)) {
            case PENDING:
                createCertificateApproveButton();
                createRejectButton();
                break;
            case IN_PROGRESS:
                createReleaseButton();
                createCancelButton();
                break;
            case RELEASING:
                createCompletedButton();
                createCancelButton();
                break;
            case REJECTED:
                createRestoreButton();
                addInvisibleButtons(1);
                break;
            case COMPLETED:
                addInvisibleButtons(2);
                break;
            default:
                createRejectButton();
                buttonContainer.getChildren().add(ButtonUtils.createInvisibleButton());
                break;
        }
    }

    private void setupBarangayIdButton(String currenStatus) {
        switch (BarangayIdStatus.fromName(currenStatus)) {
            case PENDING:
                createBarangayIdApproveButton();
                createRejectButton();
                break;
            case RELEASING:
                createCompletedButton();
                createCancelButton();
                break;
            case REJECTED:
                createRestoreButton();
                addInvisibleButtons(1);
                break;
            case COMPLETED:
                addInvisibleButtons(2);
                break;
            default:
                createRejectButton();
                buttonContainer.getChildren().add(ButtonUtils.createInvisibleButton());
                break;
        }
    }

    private void setupCedulaButton(String currentStatus) {
        switch (RequestStatus.fromName(currentStatus)) {
            case PENDING:
                createApproveButton();
                createRejectButton();
                break;
            case IN_PROGRESS:
                createReleaseButton();
                createCancelButton();
                break;
            case RELEASING:
                createCompletedButton();
                createCancelButton();
                break;
            case REJECTED:
                createRestoreButton();
                addInvisibleButtons(1);
                break;
            case COMPLETED:
                addInvisibleButtons(2);
                break;
            default:
                createRejectButton();
                buttonContainer.getChildren().add(ButtonUtils.createInvisibleButton());
                break;
        }
    }

    private void createBarangayIdApproveButton() {
        Button approve = ButtonUtils.createButton("Approve", ButtonStyle.ACCEPT, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.PRINT_ID,
                    PrintIdController.class,
                    controller -> {
                        controller.setId(requestId);
                        controller.setCallback(isSuccess -> {
                            if (isSuccess) updateRequestStatus(RequestStatus.RELEASING);
                        });
                    }
            );
        });
        setButtonState(approve, allowedActions, RolePermission.Action.APPROVE);

        buttonContainer.getChildren().add(approve);
    }

    private void createCertificateApproveButton() {
        Button approve = ButtonUtils.createButton("Approve", ButtonStyle.ACCEPT, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.PRINT_DOCUMENT,
                    PrintDocumentController.class,
                    controller -> {
                        controller.setId(requestId);
                        controller.setCallback(isSuccess -> {
                            if (isSuccess) updateRequestStatus(RequestStatus.IN_PROGRESS);
                        });
                    }
            );
        });
        setButtonState(approve, allowedActions, RolePermission.Action.APPROVE);
        buttonContainer.getChildren().add(approve);
    }

    private void createApproveButton() {
        Button approve = ButtonUtils.createButton("Approve", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Approve Request", "Would you like to approve request no." + request.getReferenceNumber() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(RequestStatus.IN_PROGRESS);
            });
        });

        setButtonState(approve, allowedActions, RolePermission.Action.APPROVE);
        buttonContainer.getChildren().add(approve);
    }

    private void createReleaseButton() {
        String head;
        String message;

        switch (request.getRequestType()) {
            case CERTIFICATES:
                head = "Release";
                message = "Would you like to release the certificate for request no." + request.getReferenceNumber() + "?";
                break;
            case BARANGAY_ID:
                head = "Release";
                message = "Would you like to release the barangay id for request no." + request.getReferenceNumber() + "?";
                break;
            case CEDULA:
                head = "Release";
                message = "Would you like to release the cedula for request no." + request.getReferenceNumber() + "?";
                break;
            default:
                head = "Release";
                message = "Would you like to release request no." + request.getReferenceNumber() + "?";
                break;
        }

        Button release = ButtonUtils.createButton("Release", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, head, message, isConfirmed -> {
                if (isConfirmed) updateRequestStatus(RequestStatus.RELEASING);
            });
        });

        setButtonState(release, allowedActions, RolePermission.Action.RELEASE);
        buttonContainer.getChildren().add(release);
    }

    private void createCompletedButton() {
        String head;
        String message;
        RequestStatus status = null;

        switch (request.getRequestType()) {
            case CERTIFICATES:
                head = "Mark as complete";
                message = "Would you like to mark cetificate request no." + request.getReferenceNumber() + " as completed?";
                status = COMPLETED;
                break;
            case BARANGAY_ID:
                head = "Mark as complete";
                message = "Would you like to mark barangay id request no." + request.getReferenceNumber() + " as completed?";
                status = COMPLETED;
                break;
            case CEDULA:
                head = "Mark as complete";
                message = "Would you like to mark cedula request no." + request.getReferenceNumber() + " as completed?";
                status = COMPLETED;
                break;
            default:
                head = "Mark as complete";
                message = "Would you like to mark request no." + request.getReferenceNumber() + " as completed?";
                break;
        }

        RequestStatus finalStatus = status;
        Button complete = ButtonUtils.createButton("Complete", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, head, message, isConfirmed -> {
                if (isConfirmed) updateRequestStatus(finalStatus);
            });
        });

        setButtonState(complete, allowedActions, RolePermission.Action.COMPLETE);

        buttonContainer.getChildren().add(complete);
    }

    private void createRestoreButton() {
        Button restore = ButtonUtils.createButton("Restore", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Restore", "Would you like to restore request #" + request.getReferenceNumber() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(RequestStatus.PENDING);
            });
        });

        setButtonState(restore, allowedActions, RolePermission.Action.RESTORE);

        buttonContainer.getChildren().add(restore);
    }

    private void createRejectButton() {
        Button reject = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Reject", "Would you like to reject request #" + request.getReferenceNumber() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(RequestStatus.REJECTED);
            });
        });

        setButtonState(reject, allowedActions, RolePermission.Action.REJECT);
        buttonContainer.getChildren().add(reject);
    }

    private void createCancelButton() {
        Button cancel = ButtonUtils.createButton("Cancel", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Cancel", "Would you like to cancel request #" + request.getReferenceNumber() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(RequestStatus.CANCELLED);
            });
        });

        setButtonState(cancel, allowedActions, RolePermission.Action.CANCEL);
        buttonContainer.getChildren().add(cancel);
    }

    private void addInvisibleButtons(int count) {
        for (int i = 0; i < count; i++) {
            buttonContainer.getChildren().add(ButtonUtils.createInvisibleButton());
        }
    }

    private void setupViewButton(RequestType requestType, String currentStatus) {
        Button viewBtn = null;
        switch (requestType) {
            case CERTIFICATES -> viewBtn = ButtonUtils.createButton("Details", ButtonStyle.VIEW, () -> {
                modalUtils.customizeModalWithCallback(
                        FXMLPath.VIEW_DOCUMENT_REQUEST,
                        ViewDocumentRequestController.class,
                        controller -> controller.setId(requestId)
                );
            });

            case BARANGAY_ID -> viewBtn = ButtonUtils.createButton("Details", ButtonStyle.VIEW, () -> {
                if (BarangayIdStatus.fromName(currentStatus) == BarangayIdStatus.COMPLETED) {
                    modalUtils.customizeModalWithCallback(
                            FXMLPath.VIEW_BARANGAY_ID,
                            ViewIdController.class,
                            controller -> controller.setId(requestId)
                    );
                } else {
                    modalUtils.customizeModalWithCallback(
                            FXMLPath.VIEW_ID_REQUEST,
                            ViewIdRequestController.class,
                            controller -> controller.setId(requestId)
                    );
                }
            });

            case CEDULA -> viewBtn = ButtonUtils.createButton("View", ButtonStyle.VIEW, () -> {
                modalUtils.customizeModalWithCallback(
                        FXMLPath.VIEW_ID_REQUEST,
                        ViewIdRequestController.class,
                        controller -> controller.setId(requestId)
                );
            });
        }
        buttonContainer.getChildren().add(viewBtn);
    }

    private void updateRequestStatus(RequestStatus status) {
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
                    case CEDULA:
                        return cedulaService.updateCedulaByStatus(requestId, CedulaStatus.fromName(status.getName()));
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

    private void setButtonState(Button button, List<RolePermission.Action> allowedActions, RolePermission.Action action) {
        if (!allowedActions.contains(action)) {
            button.setDisable(true);
//            button.setVisible(false);
//            button.setStyle("-fx-opacity: 0.5;"); // Optional: Style to indicate the button is disabled
        }
    }
}