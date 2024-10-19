package com.econnect.barangaymanagementapp.controller.shared;

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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Response;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SetupRequirementsController<T extends ApplicationController> implements BaseViewController {
    private final ModalUtils modalUtils;
    private final ImageService imageService;
    private final EmployeeService employeeService;
    private final T applicationsController;
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

    public SetupRequirementsController(DependencyInjector dependencyInjector, T applicationsController) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.imageService = dependencyInjector.getImageService();
        this.fileChooserUtils = dependencyInjector.getFileChooserUtils();
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
                        reloadTable();
                        closeWindow();
                        modalUtils.showModal(Modal.SUCCESS, "Success", "Employee " + employeeId + " has been successfully evaluated.");
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

        LocalDate selectedDate = nbiExpirationInput.getValue();

        if (selectedDate == null) {
            modalUtils.showModal(Modal.ERROR, "No Expiration Date", "Please select an expiration date for the NBI Clearance.");
            return;
        }

        try {
            selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            modalUtils.showModal(Modal.ERROR, "Invalid Date", "Please enter a valid date format.");
            return;
        }

        if (selectedDate.isBefore(LocalDate.now())) {
            modalUtils.showModal(Modal.ERROR, "Invalid Expiration Date", "The expiration date cannot be before today.");
            return;
        }

        if (selectedDate.isAfter(LocalDate.now().plusYears(1))) {
            modalUtils.showModal(Modal.ERROR, "Invalid Expiration Date", "The expiration date cannot be more than a year from today.");
            return;
        }

        nbiExpirationInput.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                LocalDate date = null;
                try {
                    date = nbiExpirationInput.getConverter().fromString(nbiExpirationInput.getEditor().getText());
                } catch (Exception e) {
                    nbiExpirationInput.getEditor().setText("");
                    nbiExpirationInput.setValue(null);
                }
                nbiExpirationInput.setValue(date);
            }
        });
        modalUtils.showModal(Modal.DEFAULT_APPROVE, "Confirm Evaluation", "Are you sure you want to evaluate this employee?", result -> {
            if (result) updateAccount();
        });
    }

    private void setupActionButtons() {
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateForm());
        uploadClearance.setOnMouseClicked(_ -> triggerFileChooser());

        nbiExpirationInput.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now()) || date.isAfter(LocalDate.now().plusYears(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ece0e1;");
                }
            }
        });
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }

    private void reloadTable() {
        applicationsController.reloadTable();
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
