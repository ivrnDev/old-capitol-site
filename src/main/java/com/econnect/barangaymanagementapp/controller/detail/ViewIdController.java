package com.econnect.barangaymanagementapp.controller.detail;

import com.econnect.barangaymanagementapp.controller.base.BaseViewController;
import com.econnect.barangaymanagementapp.domain.BarangayId;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.service.BarangayidService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

public class ViewIdController implements BaseViewController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn, profilePicture;
    @FXML
    private HBox profileContainer;
    @FXML
    private TextField residentIdInput;
    @FXML
    private Label fullNameText, addressText, genderText, birthdateText, civilStatusText, residencyStatusText, expirationDateText, emergencyFullNameText, emergencyContactText, emergencyRelationshipText, weightText, heightText;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final ResidentService residentService;
    private final BarangayidService barangayidService;
    private final ImageService imageService;
    private Image validIdImage;
    private String requestId;

    public ViewIdController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.imageService = dependencyInjector.getImageService();
        this.barangayidService = dependencyInjector.getBarangayidService();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupEventListener();
        populateFields();
    }

    private void populateFields() {
        Task<Optional<Resident>> findResidentById = new Task<>() {
            @Override
            protected Optional<Resident> call() {
                return residentService.findVerifiedResidentById(requestId.substring(0, requestId.length() - 5));
            }

            @Override
            protected void succeeded() {
                Optional<Resident> resident = getValue();
                if (resident.isPresent()) {
                    Resident residentInfo = resident.get();
                    String fullName = residentInfo.getLastName() + ", " + residentInfo.getFirstName() + " " + residentInfo.getMiddleName();
                    String emergencyFullName = residentInfo.getEmergencyLastName() + ", " + residentInfo.getEmergencyFirstName() + " " + residentInfo.getEmergencyMiddleName();
                    residentIdInput.setText(residentInfo.getId());
                    fullNameText.setText(fullName);
                    addressText.setText(residentInfo.getAddress());
                    genderText.setText(residentInfo.getSex().getName());
                    birthdateText.setText(residentInfo.getBirthdate());
                    civilStatusText.setText(residentInfo.getCivilStatus().getName());
                    residencyStatusText.setText(residentInfo.getResidencyStatus().getName());
                    expirationDateText.setText(DateFormatter.formatLocalDateToUsShortDate(LocalDate.now().plusYears(1)));
                    emergencyFullNameText.setText(emergencyFullName);
                    emergencyContactText.setText(residentInfo.getEmergencyMobileNumber());
                    emergencyRelationshipText.setText(residentInfo.getEmergencyRelationship());
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), residentInfo.getProfileUrl());
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        Task<Optional<BarangayId>> findBarangayId = new Task<>() {
            @Override
            protected Optional<BarangayId> call() {
                return barangayidService.findBarangayIdById(requestId);
            }

            @Override
            protected void succeeded() {
                Optional<BarangayId> barangayId = getValue();
                if (barangayId.isPresent()) {
                    BarangayId barangayIdInfo = barangayId.get();
                    weightText.setText(barangayIdInfo.getWeight() + " KG");
                    heightText.setText(barangayIdInfo.getHeight() + " FT");
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch barangay id: " + getException().getMessage());
            }
        };

        new Thread(findResidentById).start();
        new Thread(findBarangayId).start();
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
        closeBtn.setOnMouseClicked(_ -> closeWindow());
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.requestId = id;
    }
}
