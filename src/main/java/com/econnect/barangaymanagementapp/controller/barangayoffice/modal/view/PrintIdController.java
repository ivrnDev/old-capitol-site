package com.econnect.barangaymanagementapp.controller.barangayoffice.modal.view;

import com.econnect.barangaymanagementapp.controller.component.BaseViewController;
import com.econnect.barangaymanagementapp.domain.BarangayId;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.service.BarangayidService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.PrintUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import okhttp3.Response;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class PrintIdController implements BaseViewController {
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
    @FXML
    private VBox idContainer;
    @FXML
    private Button printBtn;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final ResidentService residentService;
    private final BarangayidService barangayidService;
    private final ImageService imageService;
    private Image validIdImage;
    private String requestId;
    private final PrintUtils printUtils;
    @Setter
    private Consumer<Boolean> callback;

    public PrintIdController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.imageService = dependencyInjector.getImageService();
        this.barangayidService = dependencyInjector.getBarangayidService();
        this.printUtils = dependencyInjector.getPrintUtils();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupEventListener();
        populateFields();
    }

    private void populateFields() {
        CountDownLatch latch = new CountDownLatch(3);
        Optional<Resident> resident = null;
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
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), residentInfo.getProfileUrl(), latch);
                }
                latch.countDown();
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
                latch.countDown();
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
                latch.countDown();
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch barangay id: " + getException().getMessage());
                latch.countDown();
            }
        };

        new Thread(findResidentById).start();
        new Thread(findBarangayId).start();

        new Thread(() -> {
            try {
                latch.await();
                Platform.runLater(() -> printBtn.setDisable(false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadProfileImage(String directory, String link, CountDownLatch latch) {
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), currentStage));
        profilePicture.setCursor(Cursor.HAND);
        profilePicture.setManaged(false);
        profilePicture.setVisible(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(profileContainer.getWidth(), profileContainer.getHeight());
        profileContainer.getChildren().add(loadingIndicator);

        Task<Image> imageTask = new Task<>() {
            @Override
            protected Image call() {
                return imageService.getImage(directory, link);
            }

            @Override
            protected void succeeded() {
                try {
                    validIdImage = getValue();
                    profilePicture.setImage(validIdImage);
                    profilePicture.setManaged(true);
                    profilePicture.setVisible(true);
                } finally {
                    profileContainer.getChildren().remove(loadingIndicator);
                    latch.countDown();
                }
            }

            @Override
            protected void failed() {
                try {
                    System.err.println("Error loading resident profile image");
                } finally {
                    profileContainer.getChildren().remove(loadingIndicator);
                    profilePicture.setManaged(true);
                    profilePicture.setVisible(true);
                    latch.countDown();
                }
            }
        };

        new Thread(imageTask).start();
    }


    private void setupEventListener() {
        closeBtn.setOnMouseClicked(_ -> closeWindow());
        printBtn.setOnMouseClicked(_ -> {
            if (printUtils.printNodeAsIdCard(idContainer, currentStage)) {
                callback.accept(true);
                closeWindow();
            }
        });
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.requestId = id;
    }

}
