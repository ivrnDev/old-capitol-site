package com.econnect.barangaymanagementapp.controller.barangayoffice.table.application;

import com.econnect.barangaymanagementapp.controller.barangayoffice.ApplicationsController;
import com.econnect.barangaymanagementapp.controller.shared.ViewEmployeeApplicationController;
import com.econnect.barangaymanagementapp.controller.shared.BaseRowController;
import com.econnect.barangaymanagementapp.controller.shared.SetupAccountController;
import com.econnect.barangaymanagementapp.controller.shared.SetupRequirementsController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.service.EmployeeService;
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

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus.fromName;


public class ApplicationRowController extends BaseRowController<Employee> {
    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private final EmployeeService employeeService;
    private final ApplicationsController applicationsController;
    private final DependencyInjector dependencyInjector;

    @FXML
    private HBox tableRow, buttonContainer;

    @FXML
    private Label residentIdLabel, lastNameLabel, firstNameLabel, statusLabel, typeLabel, dateLabel, timeLabel;

    @FXML
    private ImageView profilePicture;

    public ApplicationRowController(DependencyInjector dependencyInjector, ApplicationsController applicationsController) {
        super(dependencyInjector);
        this.dependencyInjector = dependencyInjector;
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.applicationsController = applicationsController;
    }

    public void initialize() {
        setupProfileImageClick();
        setupRowClickEvents();
        Platform.runLater(() -> setupButtonContainer());

    }

    @Override
    public void setData(Employee employeeData) {
        residentIdLabel.setText(employeeData.getId());
        lastNameLabel.setText(employeeData.getLastName());
        firstNameLabel.setText(employeeData.getFirstName());
        statusLabel.setText(employeeData.getStatus().toString());
        typeLabel.setText(employeeData.getApplicationType().toString());
        dateLabel.setText(DateFormatter.extractDateAndFormat(employeeData.getCreatedAt()));
        timeLabel.setText(DateFormatter.extractTimeAndFormat(employeeData.getCreatedAt()));
    }

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
            case UNDER_REVIEW:
                setupUnderReviewButtons();
                break;
            case EVALUATION:
                setupEvaluationButtons();
                break;
            default:
                buttonContainer.getChildren().add(ButtonUtils.createButton("View", ButtonStyle.VIEW, () -> {
                    modalUtils.customizeModalWithCallback(
                            FXMLPath.VIEW_APPLICATION_EMPLOYEE,
                            ViewEmployeeApplicationController.class,
                            controller -> controller.setId(residentIdLabel.getText())
                    );
                }));
        }
    }

    private void setupPendingButtons() {
        Button acceptBtn = ButtonUtils.createButton("Notify", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Notify", "Would you like to send an email to this employee requesting them to submit their pending requirements?", isConfirmed -> {
                if (isConfirmed) updateEmployeeToUnderReview();
            });
        });

        Button rejectBtn = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Reject", "Are you sure you want to reject this employee?", isConfirmed -> {
                if (isConfirmed) rejectEmployeeApplication();
            });
        });
        buttonContainer.getChildren().addAll(acceptBtn, rejectBtn);
    }

    private void setupUnderReviewButtons() {
        Button acceptBtn = ButtonUtils.createButton("Evaluate", ButtonStyle.ACCEPT, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.SETUP_REQUIREMENTS,
                    SetupRequirementsController.class,
                    controller -> controller.setId(residentIdLabel.getText()),
                    dependencyInjector,
                    applicationsController
            );
        });

        Button rejectBtn = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Reject", "Are you sure you want to reject this employee?", isConfirmed -> {
                if (isConfirmed) rejectEmployeeApplication();
            });
        });
        buttonContainer.getChildren().addAll(acceptBtn, rejectBtn);
    }

    private void setupEvaluationButtons() {
        Button acceptBtn = ButtonUtils.createButton("Hire", ButtonStyle.ACCEPT, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.SETUP_ACCOUNT,
                    SetupAccountController.class,
                    controller -> controller.setId(residentIdLabel.getText()),
                    dependencyInjector,
                    applicationsController
            );
        });

        Button rejectBtn = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Reject", "Are you sure you want to reject this employee?", isConfirmed -> {
                if (isConfirmed) rejectEmployeeApplication();
            });
        });
        buttonContainer.getChildren().addAll(acceptBtn, rejectBtn);
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

    private void updateEmployeeToUnderReview() {
        applicationsController.addLoadingIndicator();
        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                return employeeService.updateEmployeeToUnderReview(residentIdLabel.getText());
            }

            @Override
            protected void succeeded() {
                Response response = getValue();
                if (response.isSuccessful()) {
                    applicationsController.removeLoadingIndicator();
                    reloadTable();
                    modalUtils.showModal(Modal.SUCCESS, "Notified", "Employee + " + residentIdLabel.getText() + " has been emailed successfully.");
                } else {
                    modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while notifying employee.");
                }
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                exception.printStackTrace();
                modalUtils.showModal(Modal.ERROR, "Error", "An exception occurred: " + exception.getMessage());
            }
        };
        new Thread(task).start();
    }

    private void rejectEmployeeApplication() {
        applicationsController.addLoadingIndicator();
        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                return employeeService.rejectEmployee(residentIdLabel.getText());
            }

            @Override
            protected void succeeded() {
                Response response = getValue();
                if (response.isSuccessful()) {
                    applicationsController.removeLoadingIndicator();
                    reloadTable();
                    modalUtils.showModal(Modal.SUCCESS, "Rejected", "Employee application has been rejected.");
                } else {
                    modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while evaluating employee application.");
                }
            }
        };
        new Thread(task).start();
    }

    protected void reloadTable() {
        applicationsController.reloadTable();
    }
}