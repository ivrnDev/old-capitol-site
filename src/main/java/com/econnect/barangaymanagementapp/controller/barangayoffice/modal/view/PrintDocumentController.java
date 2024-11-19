package com.econnect.barangaymanagementapp.controller.barangayoffice.modal.view;

import com.econnect.barangaymanagementapp.controller.component.BaseViewController;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.CertificateType;
import com.econnect.barangaymanagementapp.service.CertificateService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

public class PrintDocumentController implements BaseViewController {
    @FXML
    private ImageView closeBtn, certificatePreview;
    @FXML
    private ImageView profilePicture;
    @FXML
    private HBox profileContainer;
    @FXML
    private TextField residentIdInput, requestInput, applicationTypeInput, residentTypeInput, typeInput, statusInput, referenceNumberInput, dateInput, timeInput, controlNumberInput;
    @FXML
    private TextArea purposeInput;
    @FXML
    private Text fullNameText, emailText, mobileNumberText;
    @FXML
    private Button printBtn;

    private final ModalUtils modalUtils;
    private final ResidentService residentService;
    private final CertificateService certificateService;
    private final ImageService imageService;
    private Image profilePictureImage;
    private Stage currentStage;
    private String certificateId;
    @Setter
    private Consumer<Boolean> callback;
    private Resident resident;
    private Certificate certificate;
    private File generatedPdfFile;

    public PrintDocumentController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.certificateService = dependencyInjector.getCertificateService();
        this.imageService = dependencyInjector.getImageService();
        Platform.runLater(() -> currentStage = (Stage) closeBtn.getScene().getWindow());
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeView());
        printBtn.setOnMouseClicked(_ -> handlePrint());
        Platform.runLater(this::fetchData);
    }

    private void handlePrint() {
        certificateService.printCertificate(generatedPdfFile, success -> {
            if (success) {
                callback.accept(true);
                closeView();
            }
        });
    }

    private void generateCertificate() {
        generatedPdfFile = certificateService.generateCertificate(residentIdInput.getText(), CertificateType.fromName(certificate.getRequest()), image -> {
            if (image != null) {
                certificatePreview.setImage(image);
                certificatePreview.setCursor(javafx.scene.Cursor.HAND);
                certificatePreview.setOnMouseClicked(_ -> modalUtils.showImageView(image, currentStage));
            }
        });
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
                    certificate = certificateValue.get();
                    populateRequestData(certificate);
                    generateCertificate();
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
                    resident = residentValue.get();
                    populateResidentData(resident);
//                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), resident.getProfileUrl());
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
        purposeInput.setText(certificate.getPurpose());
        statusInput.setText(certificate.getStatus().getName());
        referenceNumberInput.setText(certificate.getReferenceNumber());
        dateInput.setText(DateFormatter.formatDateToLongStyle(certificate.getCreatedAt()));
        timeInput.setText(DateFormatter.formatTimeTo12HourStyle(certificate.getCreatedAt()));
    }

    private void populateResidentData(Resident resident) {
        if (resident == null) return;
        String fullName = resident.getLastName() + ", " + resident.getFirstName() + " " + resident.getMiddleName();
        fullNameText.setText(fullName);
        emailText.setText(resident.getEmail());
        mobileNumberText.setText(resident.getMobileNumber());
    }

//    private void loadProfileImage(String directory, String link) {
//        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), currentStage));
//        profilePicture.setCursor(Cursor.HAND);
//        profilePicture.setVisible(false);
//        profilePicture.setManaged(false);
//        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(profileContainer.getWidth(), profileContainer.getHeight());
//        Platform.runLater(() -> profileContainer.getChildren().add(loadingIndicator));
//
//        Runnable call = () -> {
//            profilePictureImage = imageService.getImage(directory, link);
//            Platform.runLater(() -> {
//                profileContainer.getChildren().remove(loadingIndicator);
//                profilePicture.setImage(profilePictureImage);
//                profilePicture.setVisible(true);
//                profilePicture.setManaged(true);
//            });
//        };
//
//        Runnable onFailed = () -> {
//            Platform.runLater(() -> profileContainer.getChildren().remove(loadingIndicator));
//            profilePicture.setVisible(true);
//            profilePicture.setManaged(true);
//            System.err.println("Error loading employees");
//        };
//        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
//    }

    @FXML
    private void closeView() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.certificateId = id;
    }
}
