package com.econnect.barangaymanagementapp.controller.shared.table.employee;


import com.econnect.barangaymanagementapp.controller.shared.EmployeeController;
import com.econnect.barangaymanagementapp.controller.shared.base.BaseRowController;
import com.econnect.barangaymanagementapp.controller.shared.ViewEmployeeApplicationController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.state.UserSession;
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

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus.ACTIVE;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus.fromName;

public class EmployeeRowController extends BaseRowController<Employee> {
    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private final EmployeeService employeeService;
    private final EmployeeController employeeController;
    private final DependencyInjector dependencyInjector;
    private final UserSession userSession;

    @FXML
    private HBox tableRow, buttonContainer;

    @FXML
    private Label employeeIdLabel, lastNameLabel, firstNameLabel, positionLabel, departmentLabel, statusLabel;

    @FXML
    private ImageView profilePicture;

    public EmployeeRowController(DependencyInjector dependencyInjector, EmployeeController employeeController) {
        super(dependencyInjector);
        this.dependencyInjector = dependencyInjector;
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.employeeController = employeeController;
        this.userSession = UserSession.getInstance();
    }

    public void initialize() {
        setupProfileImageClick();
        setupRowClickEvents();
        Platform.runLater(() -> setupButtonContainer());
    }

    @Override
    protected void setData(Employee employeeData) {
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
        if (!fromName(currentStatus).equals(ACTIVE)) {
            setupInactiveButton();
            return;
        }
        setupActiveButton();
    }

    private void setupActiveButton() {
        Button updateBtn = ButtonUtils.createButton("Update", ButtonStyle.UPDATE, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Notify", "Would you like to send an email to this employee requesting them to submit their pending requirements?", isConfirmed -> {
                if (isConfirmed) updateEmployeeToUnderReview();
            });
        });

        Button terminateBtn = ButtonUtils.createButton("Terminate", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT_REJECT, "Terminate", "Are you sure you want to terminate employee" + employeeIdLabel.getText() + "?", isConfirmed -> {
                if (isConfirmed) terminateEmployee();
            });
        });
        buttonContainer.getChildren().addAll(updateBtn, terminateBtn);
    }

    private void setupInactiveButton() {
        Button restoreBtn = ButtonUtils.createButton("Restore", ButtonStyle.UPDATE, () -> {
            modalUtils.showModal(Modal.DEFAULT_APPROVE, "Notify", "Would you like to restore Employee" + employeeIdLabel.getText(), isConfirmed -> {
                if (isConfirmed) updateEmployeeToUnderReview();
            });
        });

        buttonContainer.getChildren().add(restoreBtn);
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

    private void updateEmployeeToUnderReview() {
        employeeController.addLoadingIndicator();
        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                return employeeService.updateEmployeeToUnderReview(employeeIdLabel.getText());
            }

            @Override
            protected void succeeded() {
                Response response = getValue();
                if (response.isSuccessful()) {
                    employeeController.removeLoadingIndicator();
                    reloadTable();
                    modalUtils.showModal(Modal.SUCCESS, "Notified", "Employee + " + employeeIdLabel.getText() + " has been emailed successfully.");
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

    private void terminateEmployee() {
        employeeController.addLoadingIndicator();
        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                return employeeService.terminateEmployee(employeeIdLabel.getText());
            }

            @Override
            protected void succeeded() {
                Response response = getValue();
                if (response.isSuccessful()) {
                    employeeController.removeLoadingIndicator();
                    reloadTable();
                    modalUtils.showModal(Modal.SUCCESS, "Terminated", "Employee + " + employeeIdLabel.getText() + " has been terminated successfully.");
                } else {
                    modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while evaluating employee application.");
                }
            }
        };
        new Thread(task).start();
    }

    protected void reloadTable() {
        employeeController.reloadTable();
    }
}