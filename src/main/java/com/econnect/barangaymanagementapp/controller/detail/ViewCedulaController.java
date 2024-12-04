package com.econnect.barangaymanagementapp.controller.detail;

import com.econnect.barangaymanagementapp.controller.base.BaseViewController;
import com.econnect.barangaymanagementapp.domain.Cedula;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.service.CedulaService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

public class ViewCedulaController implements BaseViewController {
    @FXML
    private ImageView closeBtn;
    @FXML
    private ImageView profilePicture;
    @FXML
    private HBox profileContainer;
    @FXML
    private TextField residentIdInput, tinNumberInput, totalEarningsInput, totalGrossInput, heightInput, weightInput, typeInput, statusInput, referenceNumberInput, dateInput, timeInput;
    @FXML
    private TextArea purposeInput;
    @FXML
    private Text fullNameText, emailText, mobileNumberText;

    private final ModalUtils modalUtils;
    private final ResidentService residentService;
    private final CedulaService cedulaService;
    private final ImageService imageService;
    private Image profilePictureImage;
    private Stage currentStage;
    private String cedulaId;

    public ViewCedulaController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.cedulaService = dependencyInjector.getCedulaService();
        this.imageService = dependencyInjector.getImageService();
        Platform.runLater(() -> currentStage = (Stage) closeBtn.getScene().getWindow());
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeView());
        Platform.runLater(this::fetchData);
        setupViewImage();
    }

    private void fetchData() {
        Task<Optional<Cedula>> requestTask = new Task<>() {
            @Override
            protected Optional<Cedula> call() {
                return cedulaService.findCedulaById(cedulaId);
            }

            @Override
            protected void succeeded() {
                Optional<Cedula> cedulaValue = getValue();
                if (cedulaValue.isPresent()) {
                    Cedula cedula = cedulaValue.get();
                    populateRequestData(cedula);
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
                return residentService.findResidentById(cedulaId.substring(0, cedulaId.length() - 5));
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

    private void populateRequestData(Cedula cedula) {
        if (cedula == null) return;
        residentIdInput.setText(cedula.getId().substring(0, cedula.getId().length() - 5));
        typeInput.setText("Cedula");
        purposeInput.setText(cedula.getPurpose());
        heightInput.setText(cedula.getHeight());
        weightInput.setText(cedula.getWeight());
        totalGrossInput.setText(cedula.getGrossReceipt());
        totalEarningsInput.setText(cedula.getTotalEarnings());
        statusInput.setText(cedula.getStatus().getName());
        referenceNumberInput.setText(cedula.getReferenceNumber());
        dateInput.setText(DateFormatter.formatDateToLongStyle(cedula.getCreatedAt()));
        timeInput.setText(DateFormatter.formatTimeTo12HourStyle(cedula.getCreatedAt()));
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
        this.cedulaId = id;
    }
}
