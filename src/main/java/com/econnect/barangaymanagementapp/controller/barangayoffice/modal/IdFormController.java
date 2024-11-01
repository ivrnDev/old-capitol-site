package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.domain.BarangayId;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.service.BarangayidService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FormValidator;
import com.econnect.barangaymanagementapp.util.Validator;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEFAULT_PROFILE;

//TODO: ADD VALIDATION WHERE RESIDENT CAN ONLY REQUEST ONCE A YEAR
public class IdFormController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn, profilePicture;
    @FXML
    private Button cancelBtn, confirmBtn;
    @FXML
    private TextField residentIdInput, weightInput, heightInput;
    @FXML
    private HBox profileContainer;
    @FXML
    private Label fullNameText, addressText, genderText, birthdateText, civilStatusText, residencyStatusText, expirationDateText, emergencyFullNameText, emergencyContactText, emergencyRelationshipText, addressExtension, addressExtension1;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final FormValidator formValidator;
    private final BarangayidService barangayidService;
    private final ResidentService residentService;
    private final ImageService imageService;
    private final Validator validator;
    private Image validIdImage;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private boolean residentExists = false;

    public IdFormController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.barangayidService = dependencyInjector.getBarangayidService();
        this.residentService = dependencyInjector.getResidentService();
        this.imageService = dependencyInjector.getImageService();
        this.validator = dependencyInjector.getValidator();
        this.formValidator = dependencyInjector.getFormValidator();
        Platform.runLater(() -> this.currentStage = (Stage) confirmBtn.getScene().getWindow());
    }

    public void initialize() {
        residentIdInput.requestFocus();
        setupEventListener();
        initializeText();
    }

    private void submitData() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Task<Void> addRequest = new Task<>() {
            @Override
            protected Void call() {
                try {
                    BarangayId requests = createRequestsFromInput();
                    barangayidService.createBarangayId(requests);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error during request creation", e);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                closeWindow();
                modalUtils.showModal(Modal.SUCCESS, "Success", "Barangay ID Request has been successfully added.");
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while adding the request."));
            }
        };

        new Thread(addRequest).start();
    }

    private BarangayId createRequestsFromInput() {
        return BarangayId.builder()
                .id(residentIdInput.getText())
                .weight(weightInput.getText().trim().replace(" KG", ""))
                .height(heightInput.getText().trim().replace(" FT", ""))
                .expirationDate(expirationDateText.getText())
                .build();
    }

    private void populateFields(String residentId) {
        residentExists = false;
        if (residentId.isEmpty()) {
            clearInputFields();
            return;
        }

        Task<Optional<Resident>> residentTask = new Task<>() {
            @Override
            protected Optional<Resident> call() {
                return residentService.findActiveResidentById(residentId);
            }

            @Override
            protected void succeeded() {
                Optional<Resident> resident = getValue();
                if (resident.isPresent()) {
                    residentExists = true;
                    Resident residentInfo = resident.get();
                    String fullName = residentInfo.getLastName() + ", " + residentInfo.getFirstName() + " " + residentInfo.getMiddleName();
                    String emergencyFullName = residentInfo.getEmergencyLastName() + ", " + residentInfo.getEmergencyFirstName() + " " + residentInfo.getEmergencyMiddleName();
                    fullNameText.setText(fullName);
                    showAddress();
                    addressText.setText(residentInfo.getAddress());
                    genderText.setText(residentInfo.getSex().getName());
                    birthdateText.setText(residentInfo.getBirthdate());
                    civilStatusText.setText(residentInfo.getCivilStatus().getName());
                    residencyStatusText.setText(residentInfo.getResidencyStatus().getName());
                    expirationDateText.setText(DateFormatter.toStringLocaleDateFormat(LocalDate.now().plusYears(1)));
                    emergencyFullNameText.setText(emergencyFullName);
                    emergencyContactText.setText(residentInfo.getEmergencyMobileNumber());
                    emergencyRelationshipText.setText(residentInfo.getEmergencyRelationship());
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), residentInfo.getProfileUrl());
                } else {
                    clearInputFields();
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };
        new Thread(residentTask).start();
    }

    private void initializeText() {
        profilePicture.setOnMouseClicked(null);
        profilePicture.setCursor(Cursor.DEFAULT);
        profilePicture.setImage(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath()))));
        validIdImage = null;
        fullNameText.setText("");
        addressText.setText("");
        hideAddress();
        genderText.setText("");
        birthdateText.setText("");
        civilStatusText.setText("");
        residencyStatusText.setText("");
        expirationDateText.setText("");
        emergencyFullNameText.setText("");
        emergencyContactText.setText("");
        emergencyRelationshipText.setText("");
    }

    private void clearInputFields() {
        profilePicture.setOnMouseClicked(null);
        profilePicture.setCursor(Cursor.DEFAULT);
        profilePicture.setImage(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath()))));
        validIdImage = null;
        fullNameText.setText("");
        hideAddress();
        addressText.setText("");
        genderText.setText("");
        birthdateText.setText("");
        civilStatusText.setText("");
        residencyStatusText.setText("");
        expirationDateText.setText("");
        emergencyFullNameText.setText("");
        emergencyContactText.setText("");
        emergencyRelationshipText.setText("");
    }

    private void validateData() {
        TextField[] textFields = {residentIdInput, weightInput, heightInput};
        boolean hasError = validator.hasEmptyFields(textFields);
        if (hasError) return;

        if (!residentExists) {
            residentIdInput.setStyle("-fx-border-color: red");
            modalUtils.showModal(Modal.ERROR, "Resident Not Found", "Resident ID does not exist.");
            return;
        }

        submitData();
    }

    private void loadProfileImage(String directory, String link) {
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), currentStage));
        profilePicture.setCursor(Cursor.HAND);
        profilePicture.setManaged(false);
        profilePicture.setVisible(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(profileContainer.getWidth(), profileContainer.getHeight());
        Platform.runLater(() -> profileContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            validIdImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                profileContainer.getChildren().remove(loadingIndicator);
                profilePicture.setImage(validIdImage);
                profilePicture.setManaged(true);
                profilePicture.setVisible(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> profileContainer.getChildren().remove(loadingIndicator));
            profilePicture.setManaged(true);
            profilePicture.setVisible(true);
            System.err.println("Error loading resident");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void setupEventListener() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateData());
        residentIdInput.setOnKeyPressed(_ -> {
            residentIdInput.textProperty().addListener((_, _, newValue) -> {
                searchDelay.setOnFinished(_ -> populateFields(newValue));
                searchDelay.playFromStart();
            });
        });
        validator.createHeightFormatter(heightInput);
        validator.createWeightFormatter(weightInput);
        validator.setupResidentIdInput(residentIdInput);
    }

    private void hideAddress() {
        addressExtension.setVisible(false);
        addressExtension1.setVisible(false);
    }

    private void showAddress() {
        addressExtension.setVisible(true);
        addressExtension1.setVisible(true);
    }

    public void closeWindow() {
        formValidator.removeListeners();
        modalUtils.closeCustomizeModal();
    }

    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();
        });
    }
}
