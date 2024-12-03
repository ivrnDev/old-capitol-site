package com.econnect.barangaymanagementapp.controller.table.request;

import com.econnect.barangaymanagementapp.controller.base.BaseRowController;
import com.econnect.barangaymanagementapp.controller.detail.*;
import com.econnect.barangaymanagementapp.domain.DepartmentRequest;
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
import com.econnect.barangaymanagementapp.service.DepartmentRequestService;
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


public class DepartmentRequestRowController extends BaseRowController<DepartmentRequest> {
    @FXML
    private HBox tableRow, buttonContainer;
    @FXML
    private Label requestIdLabel, residentNameLabel, departmentLabel, statusLabel, typeLabel, timestampLabel;

    private final ModalUtils modalUtils;
    private final DepartmentRequestService departmentRequestService;
    private RoleType roleType;
    private List<RolePermission.Action> allowedActions;
    @Getter
    private String requestId;
    @Getter
    private DepartmentRequest request;

    public DepartmentRequestRowController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.modalUtils = dependencyInjector.getModalUtils();
        this.departmentRequestService = dependencyInjector.getDepartmentRequestService();
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
    protected void setData(DepartmentRequest request) {
        this.request = request;
        this.requestId = request.getId();
        Platform.runLater(() -> setupButtonContainer());
        requestIdLabel.setText(request.getId());
        residentNameLabel.setText(request.getName());
        departmentLabel.setText("Health Department");
        statusLabel.setText(request.getStatus());
        typeLabel.setText(request.getType());
        timestampLabel.setText(request.getTimestamp());
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
        setupViewButton(currentStatus);
        setupButton(currentStatus);
    }

    private void setupButton(String currentStatus) {
        switch (RequestStatus.fromName(currentStatus)) {
            case PENDING:
                createApproveButton();
                createRejectButton();
                break;
            default:
                addInvisibleButtons(2);
                break;
        }
    }

    private void createApproveButton() {
        Button approve = ButtonUtils.createButton("Approve", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Approve Request", "Would you like to approve request no." + request.getId() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(RequestStatus.IN_PROGRESS.getName());
            });
        });

        setButtonState(approve, allowedActions, RolePermission.Action.APPROVE);
        buttonContainer.getChildren().add(approve);
    }

    private void createRestoreButton() {
        Button restore = ButtonUtils.createButton("Restore", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Restore", "Would you like to restore request #" + request.getId() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(RequestStatus.PENDING.getName());
            });
        });

        setButtonState(restore, allowedActions, RolePermission.Action.RESTORE);

        buttonContainer.getChildren().add(restore);
    }

    private void createRejectButton() {
        Button reject = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Reject", "Would you like to reject request #" + request.getId() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(RequestStatus.REJECTED.getName());
            });
        });

        setButtonState(reject, allowedActions, RolePermission.Action.REJECT);
        buttonContainer.getChildren().add(reject);
    }

    private void createCancelButton() {
        Button cancel = ButtonUtils.createButton("Cancel", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Cancel", "Would you like to cancel request #" + request.getId() + "?", isConfirmed -> {
                if (isConfirmed) updateRequestStatus(RequestStatus.CANCELLED.getName());
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

    private void setupViewButton(String currentStatus) {
        Button viewBtn = ButtonUtils.createButton("Details", ButtonStyle.VIEW, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.VIEW_DOCUMENT_REQUEST,
                    ViewDocumentRequestController.class,
                    controller -> controller.setId(requestId)
            );
        });
        buttonContainer.getChildren().add(viewBtn);
    }

    private void updateRequestStatus(String status) {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(tableRow.getWidth(), tableRow.getHeight());

        tableRow.getChildren().forEach(node -> {
            node.setVisible(false);
            node.setManaged(false);
        });
        tableRow.getChildren().add(loadingIndicator);

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                return departmentRequestService.updateDepartmentRequestByStatus(requestId, status);
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