package com.econnect.barangaymanagementapp.controller.form;

import com.econnect.barangaymanagementapp.domain.Cedula;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.service.CedulaService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.UploadImageUtils;
import com.econnect.barangaymanagementapp.util.Validator;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.Response;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class BorrowController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn, tinIdPreview;
    @FXML
    private Button cancelBtn, confirmBtn;
    @FXML
    private TextField residentIdInput, nameInput, addressInput, sexInput, citizenshipInput, civilStatusInput, birthdateInput, heightInput, weightInput,
            occupationInput, tinNumberInput, grossReceiptInput, totalEarningsInput;
    @FXML
    private TextArea purposeInput;
    @FXML
    private HBox uploadTinId, viewTinId, tinIdPreviewContainer;
    @FXML
    private Label tinIdLabel;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final Validator validator;
    private final UploadImageUtils uploadImageUtils;
    private final CedulaService cedulaService;
    private final ResidentService residentService;
    private final ImageService imageService;
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private boolean residentExists = false;
    private boolean hasTinID;
    private Image tinIdImage;

    public BorrowController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.cedulaService = dependencyInjector.getCedulaService();
        this.residentService = dependencyInjector.getResidentService();
        this.validator = dependencyInjector.getValidator();
        this.uploadImageUtils = dependencyInjector.getUploadImageUtils();
        this.imageService = dependencyInjector.getImageService();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupEventListener();
        ImageUtils.setRoundedClip(tinIdPreview, 10, 10);
    }

    private void submitData() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        CountDownLatch latch = new CountDownLatch(2);
        boolean[] successFlags = {false, false};

        Task<Response> addCedulaTask = new Task<>() {
            @Override
            protected Response call() {
                Cedula cedula = createRequestsFromInput();
                return cedulaService.createCedula(cedula);
            }

            @Override
            protected void succeeded() {
                latch.countDown();
                if (getValue().isSuccessful()) {
                    successFlags[0] = true;
                } else {
                    modalUtils.showModal(Modal.ERROR, "Failed", "Failed to submit Cedula request.");
                }
            }

            @Override
            protected void failed() {
                latch.countDown();
                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while submitting Cedula request.");
            }
        };

        Task<Response> updateResidentTin = new Task<>() {
            @Override
            protected Response call() {
                String tinIdPath = imageService.uploadImage(Firestore.TIN_ID, tinIdImage, residentIdInput.getText());
                return residentService.updateResidentTin(residentIdInput.getText(), tinNumberInput.getText(), tinIdPath);
            }

            @Override
            protected void succeeded() {
                latch.countDown();
                if (getValue().isSuccessful()) {
                    successFlags[1] = true;
                } else {
                    modalUtils.showModal(Modal.ERROR, "Error", "Failed to update Tin ID.");
                }
            }

            @Override
            protected void failed() {
                latch.countDown();
                modalUtils.showModal(Modal.ERROR, "Error", "Failed to update Tin ID.");
            }
        };

        new Thread(addCedulaTask).start();
        new Thread(updateResidentTin).start();

        new Thread(() -> {
            try {
                latch.await();
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    rootPane.getChildren().remove(loadingIndicator);
                    if (successFlags[0] && successFlags[1]) {
                        closeWindow();
                        modalUtils.showModal(Modal.SUCCESS, "Success", "Cedula request has been submitted successfully.");
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Cedula createRequestsFromInput() {
        return Cedula.builder()
                .id(residentIdInput.getText())
                .grossReceipt(grossReceiptInput.getText())
                .totalEarnings(totalEarningsInput.getText())
                .height(heightInput.getText())
                .weight(weightInput.getText())
                .build();
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
                return residentService.findVerifiedResidentById(residentId);
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
                    String fullName = lastName + ", " + firstName + " " + middleName;
                    nameInput.setText(fullName);
                    addressInput.setText(residentInfo.getAddress());
                    birthdateInput.setText(residentInfo.getBirthdate().toString());
                    occupationInput.setText(residentInfo.getOccupation());
                    sexInput.setText(residentInfo.getSex().getName());
                    citizenshipInput.setText(residentInfo.getCitizenship());
                    civilStatusInput.setText(residentInfo.getCivilStatus().getName());
                    tinNumberInput.setText(residentInfo.getTinIdNumber());
                    tinNumberInput.setStyle(null);
                    if (!residentInfo.getTinIdNumber().isEmpty()) {
                        tinNumberInput.setEditable(false);
                        tinNumberInput.setMouseTransparent(true);
                        loadTinId(Firestore.TIN_ID.getPath(), residentInfo.getTinIdUrl());
                        hideUploadTin();
                    }
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
        nameInput.clear();
        addressInput.clear();
        birthdateInput.clear();
        occupationInput.clear();
        sexInput.clear();
        citizenshipInput.clear();
        civilStatusInput.clear();
        tinNumberInput.clear();
        tinNumberInput.setEditable(true);
        tinIdPreview.setImage(null);
        tinIdImage = null;
        uploadTinId.setManaged(true);
        uploadTinId.setVisible(true);
        viewTinId.setVisible(false);
    }

    private void validateData() {

        if (validator.validate(residentIdInput, Validator.VALIDATOR_TYPE.IS_EMPTY)) {
            residentIdInput.requestFocus();
            residentIdInput.setStyle("-fx-border-color: red;");
            modalUtils.showModal(Modal.ERROR, "Empty Resident ID", "Please enter a Resident ID.");
            return;
        } else {
            residentIdInput.setStyle(null);
        }

        if (!residentExists) {
            modalUtils.showModal(Modal.ERROR, "Resident Not Found", "Resident ID does not exist.");
            return;
        }

        if (validator.hasEmptyFields(List.of(grossReceiptInput, totalEarningsInput, heightInput, weightInput), List.of(purposeInput)))
            return;

        if (validator.hasEmptyImages(new Image[]{tinIdImage}, new HBox[]{uploadTinId})) return;

        submitData();
    }

    private void loadTinId(String directory, String link) {
        viewTinId.setOnMouseClicked(_ -> {
            if (tinIdImage != null) {
                modalUtils.showImageView(tinIdImage, currentStage);
            }
        });
        tinIdPreview.setVisible(false);
        tinIdPreview.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(tinIdPreviewContainer.getWidth(), tinIdPreviewContainer.getHeight());
        Platform.runLater(() -> tinIdPreviewContainer.getChildren().add(loadingIndicator));
        Runnable call = () -> {
            tinIdImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                tinIdPreviewContainer.getChildren().remove(loadingIndicator);
                tinIdPreview.setImage(tinIdImage);
                viewTinId.setCursor(Cursor.HAND);
                tinIdPreview.setVisible(true);
                tinIdPreview.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> {
                tinIdPreviewContainer.getChildren().remove(loadingIndicator);

            });
            tinIdPreview.setVisible(true);
            tinIdPreview.setManaged(true);

            System.err.println("Error loading image");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void setupEventListener() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateData());

        validator.createHeightFormatter(heightInput);
        validator.createWeightFormatter(weightInput);
        validator.setupResidentIdInput(residentIdInput);
        residentIdInput.setOnKeyPressed(_ -> {
            residentIdInput.textProperty().addListener((_, _, newValue) -> {
                searchDelay.setOnFinished(_ -> populateInputFields(newValue));
                searchDelay.playFromStart();
            });
        });
        validator.createTinIdFormatter(tinNumberInput);
        validator.setupCurrencyListener(grossReceiptInput, totalEarningsInput);

        uploadTinId.setOnMouseClicked(_ -> uploadImageUtils.loadSetupFile(currentStage, image -> {
            tinIdImage = image;
            tinIdPreview.setImage(image);
            tinIdLabel.setText(DateFormatter.toTimeStamp(ZonedDateTime.now()) + ".jpg");
            viewTinId.setVisible(true);
            uploadTinId.setStyle(null);
        }));

        viewTinId.setOnMouseClicked(_ -> modalUtils.showImageView(tinIdPreview.getImage(), currentStage));
    }

    private void hideUploadTin() {
        uploadTinId.setVisible(false);
        uploadTinId.setManaged(false);
        viewTinId.setVisible(true);
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
