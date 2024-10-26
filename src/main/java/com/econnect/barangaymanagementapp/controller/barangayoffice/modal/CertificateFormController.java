package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEFAULT_PROFILE;

public class CertificateFormController {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView closeBtn, profilePicture;

    @FXML
    private Button cancelBtn, confirmBtn;

    @FXML
    private HBox viewGovernmentID, profileContainer;

    @FXML
    private TextField residentIdInput, nameInput, addressInput, emailInput, contactNumberInput, birthdateInput, occupationInput, sexInput;

    @FXML
    private TextArea purposeInput;

    @FXML
    private CheckBox clearanceCheckBox, indigencyCheckBox, cedulaCheckBox, bankCertificateCheckBox, businessPermitCheckBox, financialCheckBox;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final ResidentService residentService;
    private final ImageService imageService;
    private Image validIdImage;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private boolean residentExists = false;

    public CertificateFormController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.imageService = dependencyInjector.getImageService();
        Platform.runLater(() -> this.currentStage = (Stage) confirmBtn.getScene().getWindow());
    }

    public void initialize() {
        setupEventListener();
        setupViewImage();
    }

    private void populateInputFields(String residentId) {
        residentExists = false;
        if (residentId.isEmpty()) {
            clearInputFields();
            return;
        }

        Task<Optional<Resident>> residentTask = new Task<>() {
            @Override
            protected Optional<Resident> call() {
                return residentService.findResidentById(residentId);
            }

            @Override
            protected void succeeded() {
                Optional<Resident> resident = getValue();
                if (resident.isPresent()) {
                    residentExists = true;
                    Resident residentInfo = resident.get();
                    String firstName = residentInfo.getFirstName();
                    String lastName = residentInfo.getLastName();
                    String middleName = residentInfo.getMiddleName();
                    String fullName = firstName + " " + middleName + " " + lastName;
                    nameInput.setText(fullName);
                    addressInput.setText(residentInfo.getAddress());
                    birthdateInput.setText(residentInfo.getBirthdate().toString());
                    emailInput.setText(residentInfo.getEmail());
                    contactNumberInput.setText(residentInfo.getContactNumber());
                    occupationInput.setText(residentInfo.getOccupation());
                    sexInput.setText(residentInfo.getSex().getName());
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), residentInfo.getProfileUrl());
                    loadValidId(Firestore.VALID_ID.getPath(), residentInfo.getValidIdURL());
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

    private void clearInputFields() {
        profilePicture.setImage(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath()))));
        validIdImage = null;
        viewGovernmentID.setOnMouseClicked(null);
        viewGovernmentID.setCursor(Cursor.DEFAULT);
        nameInput.clear();
        addressInput.clear();
        birthdateInput.clear();
        emailInput.clear();
        contactNumberInput.clear();
        occupationInput.clear();
        sexInput.clear();
    }

    private void loadProfileImage(String directory, String link) {
        profilePicture.setVisible(false);
        profilePicture.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(profileContainer.getWidth(), profileContainer.getHeight());
        Platform.runLater(() -> profileContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            validIdImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                profileContainer.getChildren().remove(loadingIndicator);
                profilePicture.setImage(validIdImage);
                profilePicture.setVisible(true);
                profilePicture.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> profileContainer.getChildren().remove(loadingIndicator));
            profilePicture.setVisible(true);
            profilePicture.setManaged(true);
            System.err.println("Error loading employees");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void loadValidId(String directory, String link) {
        Task<Image> task = new Task<>() {
            @Override
            protected Image call() {
                return imageService.getImage(directory, link);
            }

            @Override
            protected void succeeded() {
                viewGovernmentID.setCursor(Cursor.HAND);
                viewGovernmentID.setOnMouseClicked(_ -> modalUtils.showImageView(getValue(), currentStage));
            }

            @Override
            protected void failed() {
                viewGovernmentID.setCursor(Cursor.DEFAULT);

                System.err.println("Error loading valid ID");
            }
        };
        new Thread(task).start();
    }

    private void setupViewImage() {
        ImageUtils.setCircleClip(profilePicture);
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), currentStage));
    }

    private void setupEventListener() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> closeWindow());
        residentIdInput.setOnKeyPressed(_ -> {
            residentIdInput.textProperty().addListener((_, _, newValue) -> {
                searchDelay.setOnFinished(_ -> populateInputFields(newValue));
                searchDelay.playFromStart();
            });
        });
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }

    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();
        });
    }
}
