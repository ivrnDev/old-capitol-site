package com.econnect.barangaymanagementapp.controller.barangayoffice.modal.view;

import com.econnect.barangaymanagementapp.controller.component.BaseViewController;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.service.CertificateService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

public class ViewDocumentRequestController implements BaseViewController {
    @FXML
    private ImageView closeBtn;
    @FXML
    private ImageView profilePicture;
    @FXML
    private HBox profileContainer;
    @FXML
    private TextField residentIdInput, requestInput, applicationTypeInput, residentTypeInput, typeInput, statusInput, referenceNumberInput;
    @FXML
    private Text fullNameText, emailText, mobileNumberText;

    private final ModalUtils modalUtils;
    private final ResidentService residentService;
    private final CertificateService certificateService;
    private final ImageService imageService;
    private Image profilePictureImage;
    private Stage currentStage;
    private String certificateId;

    public ViewDocumentRequestController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.certificateService = dependencyInjector.getCertificateService();
        this.imageService = dependencyInjector.getImageService();
        Platform.runLater(() -> currentStage = (Stage) closeBtn.getScene().getWindow());
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeView());
//        profilePicture.setOnMouseClicked(_ -> handleClickProfile());
        fetchData();
        setupViewImage();
    }

    private void fetchData() {
        Task<Optional<Certificate>> requestTask = new Task<>() {
            @Override
            protected Optional<Certificate> call() {
                return certificateService.findCertificateById(certificateId);
            }

            @Override
            protected void succeeded() {
                Optional<Certificate> certificateValue = getValue();
                if (certificateValue.isPresent()) {
                    Certificate certificate = certificateValue.get();
                    populateRequestData(certificate);
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        Task<Optional<Resident>> fetchResident = new Task<>() {
            @Override
            protected Optional<Resident> call() {
                return residentService.findResidentById(certificateId.substring(0, certificateId.length() - 5));
            }

            @Override
            protected void succeeded() {
                Optional<Resident> residentValue = getValue();
                if (residentValue.isPresent()) {
                    Resident resident = residentValue.get();
                    populateResidentData(resident);
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), resident.getProfileUrl());
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        new Thread(fetchResident).start();
        new Thread(requestTask).start();
    }

    private void populateRequestData(Certificate certificate) {
        if (certificate == null) return;
        residentIdInput.setText(certificate.getId().substring(0, certificate.getId().length() - 5));
        requestInput.setText(certificate.getRequest());
        applicationTypeInput.setText(certificate.getApplicationType().getName());
        residentTypeInput.setText(certificate.getRequestorType());
        typeInput.setText("Certificate");
        statusInput.setText(certificate.getStatus().getName());
        referenceNumberInput.setText(certificate.getReferenceNumber());
    }

    private void populateResidentData(Resident resident) {
        if (resident == null) return;
        String fullName = resident.getLastName() + ", " + resident.getFirstName() + " " + resident.getMiddleName();
        fullNameText.setText(fullName);
        emailText.setText(resident.getEmail());
        mobileNumberText.setText(resident.getMobileNumber());
    }

    private void loadProfileImage(String directory, String link) {
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), currentStage));
        profilePicture.setCursor(Cursor.HAND);
        profilePicture.setVisible(false);
        profilePicture.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(profileContainer.getWidth(), profileContainer.getHeight());
        Platform.runLater(() -> profileContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            profilePictureImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                profileContainer.getChildren().remove(loadingIndicator);
                profilePicture.setImage(profilePictureImage);
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

    private void setupViewImage() {
        Platform.runLater(() -> ImageUtils.setRoundedClip(profilePicture, 25, 25));
    }

    @FXML
    private void closeView() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.certificateId = id;
    }
}
