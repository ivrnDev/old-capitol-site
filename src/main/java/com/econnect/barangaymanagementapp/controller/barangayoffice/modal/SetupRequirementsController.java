package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.controller.barangayoffice.ApplicationsController;
import com.econnect.barangaymanagementapp.controller.component.BaseViewController;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.FileChooserUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Response;

import java.io.File;

public class SetupRequirementsController implements BaseViewController {
    private final ModalUtils modalUtils;
    private final ImageService imageService;
    private final EmployeeService employeeService;
    private final ApplicationsController applicationsController;
    private final FileChooserUtils fileChooserUtils;
    private Stage currentStage;
    private String employeeId;
    private File clearanceFile;

    @FXML
    private AnchorPane rootContainer;

    @FXML
    private HBox uploadClearance;

    @FXML
    private DatePicker nbiExpirationInput;

    @FXML
    private Label clearanceLabel;

    @FXML
    private Button confirmBtn, cancelBtn;

    public SetupRequirementsController(DependencyInjector dependencyInjector, ApplicationsController applicationsController) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.imageService = dependencyInjector.getImageService();
        this.fileChooserUtils = dependencyInjector.getFileChooser();
        this.applicationsController = applicationsController;
        Platform.runLater(() -> this.currentStage = (Stage) confirmBtn.getScene().getWindow());
    }

    public void initialize() {
        setupActionButtons();
    }

    private void updateAccount() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootContainer.getWidth(), rootContainer.getHeight());
        Platform.runLater(() -> rootContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            String nbiClearanceURL = imageService.uploadImage(Firestore.NBI_CLEARANCE, clearanceFile, employeeId);

            try {
                Response response = employeeService.updateEmployeeToEvaluation(employeeId, nbiClearanceURL, String.valueOf(nbiExpirationInput.getValue()));
                Platform.runLater(() -> {
                    rootContainer.getChildren().remove(loadingIndicator);
                    if (response.isSuccessful()) {
                        applicationsController.populateApplicationRows();
                        closeWindow();
                        modalUtils.showModal(Modal.SUCCESS, "Success", "Employee + " + employeeId + " has been successfully evaluated.");
                    } else {
                        closeWindow();
                        modalUtils.showModal(Modal.ERROR, "Failed", "An error occurred while activating employee application.");
                    }
                });
            } catch (Exception e) {
                modalUtils.showModal(Modal.ERROR, "Error", "An exception occurred while activating employee application.");
                e.printStackTrace();
                Platform.runLater(() -> rootContainer.getChildren().remove(loadingIndicator));
            }
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> rootContainer.getChildren().remove(loadingIndicator));
            System.err.println("Failed to activate employee");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void triggerFileChooser() {
        FileChooser fileChooser = fileChooserUtils.createFileChooser();

        File file = fileChooser.showOpenDialog(currentStage);

        if (file != null) {
            clearanceFile = file;
            clearanceLabel.setText(file.getName());
        } else {
            Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "No File Selected", "Please select a valid file."));
        }
    }

    private void validateForm() {
        if (clearanceFile == null) {
            modalUtils.showModal(Modal.ERROR, "No File Selected", "Please select a valid file.");
            return;
        }

        if (nbiExpirationInput.getValue() == null) {
            modalUtils.showModal(Modal.ERROR, "No Expiration Date", "Please select an expiration date for the NBI Clearance.");
            return;
        }

        modalUtils.showModal(Modal.DEFAULT_APPROVE, "Confirm Evaluation", "Are you sure you want to evaluate this employee?", result -> {
            if (result) updateAccount();
        });
    }

    private void setupActionButtons() {
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateForm());
        uploadClearance.setOnMouseClicked(_ -> triggerFileChooser());
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }

    @FXML
    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();
        });
    }

    @Override
    public void setId(String id) {
        this.employeeId = id;
    }
}
