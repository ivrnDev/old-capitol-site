package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.CertificateService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.Validator;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEFAULT_DOCUMENT;
import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEFAULT_PROFILE;

public class CertificateFormController {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView closeBtn, profilePicture, governmentIdPreview;

    @FXML
    private Button cancelBtn, confirmBtn;

    @FXML
    private HBox viewGovernmentID, profileContainer, purposeContainer, certificateContainer, governmentIdPreviewContainer;

    @FXML
    private TextField residentIdInput, nameInput, addressInput, emailInput, contactNumberInput, birthdateInput, occupationInput, sexInput;

    @FXML
    private TextArea purposeInput;

    @FXML
    private CheckBox clearanceCheckBox, indigencyCheckBox, residencyComboBox;

    @FXML
    private RadioButton studentRadio, seniorRadio, pwdRadio, jobSeekerRadio;

    @FXML
    private ToggleGroup residentTypeRadio;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final Validator validator;
    private final CertificateService certificateService;
    private final ResidentService residentService;
    private final ImageService imageService;
    private Image governmentIdImage;
    private Image profilePictureImage;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private boolean residentExists = false;

    public CertificateFormController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.certificateService = dependencyInjector.getCertificateService();
        this.residentService = dependencyInjector.getResidentService();
        this.imageService = dependencyInjector.getImageService();
        this.validator = dependencyInjector.getValidator();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupEventListener();
        setupViewImage();
    }

    private void submitData() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Task<Void> addResidentTask = new Task<>() {
            @Override
            protected Void call() {
                Certificate certificate = createRequestsFromInput();
                certificateService.createCertificate(certificate);
                return null;
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                closeWindow();
                modalUtils.showModal(Modal.SUCCESS, "Success", "Certificate Request has been successfully added.");
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while adding the resident."));
            }
        };

        new Thread(addResidentTask).start();
    }

    private Certificate createRequestsFromInput() {
        List<CheckBox> checkBoxes = List.of(clearanceCheckBox, indigencyCheckBox, residencyComboBox);

        StringBuilder requests = new StringBuilder();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                requests.append(checkBox.getText()).append(", ");
            }
        }

        if (requests.length() > 0) {
            requests.setLength(requests.length() - 2);
        }

        return Certificate.builder()
                .id(residentIdInput.getText())
                .requestorType(((RadioButton) residentTypeRadio.getSelectedToggle()).getText())
                .request(String.valueOf(requests))
                .purpose(indigencyCheckBox.isSelected() ? purposeInput.getText() : "")
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
                return residentService.findAllVerifiedResidents(residentId);
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
                    contactNumberInput.setText(residentInfo.getMobileNumber());
                    occupationInput.setText(residentInfo.getOccupation());
                    sexInput.setText(residentInfo.getSex().getName());
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), residentInfo.getProfileUrl());
                    loadValidId(Firestore.VALID_ID.getPath(), residentInfo.getValidIdUrl());
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
        profilePicture.setOnMouseClicked(null);
        profilePicture.setCursor(Cursor.DEFAULT);
        profilePicture.setImage(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath()))));
        profilePictureImage = null;

        viewGovernmentID.setOnMouseClicked(null);
        viewGovernmentID.setCursor(Cursor.DEFAULT);
        governmentIdPreview.setImage(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_DOCUMENT.getFxmlPath()))));
        governmentIdImage = null;

        nameInput.clear();
        addressInput.clear();
        birthdateInput.clear();
        emailInput.clear();
        contactNumberInput.clear();
        occupationInput.clear();
        sexInput.clear();
    }

    private void validateData() {
        CheckBox[] checkBoxes = {clearanceCheckBox, indigencyCheckBox, residencyComboBox};

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

        if (validator.hasEmptyCheckBox(checkBoxes, certificateContainer)) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Please select at least one certificate.");
            return;
        }

        if (indigencyCheckBox.isSelected()) {
            if (purposeInput.getText().isEmpty()) {
                purposeInput.requestFocus();
                purposeInput.getStyleClass().add("error");
                modalUtils.showModal(Modal.ERROR, "Empty Purpose", "Please enter a purpose for the indigency certificate.");
                return;
            } else {
                purposeInput.getStyleClass().remove("error");
            }
        }

        submitData();
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

    private void loadValidId(String directory, String link) {
        viewGovernmentID.setOnMouseClicked(_ -> {
            if (governmentIdImage != null) {
                modalUtils.showImageView(governmentIdImage, currentStage);
            }
        });
        governmentIdPreview.setVisible(false);
        governmentIdPreview.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(governmentIdPreviewContainer.getWidth(), governmentIdPreviewContainer.getHeight());
        Platform.runLater(() -> governmentIdPreviewContainer.getChildren().add(loadingIndicator));
        Runnable call = () -> {
            governmentIdImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                governmentIdPreviewContainer.getChildren().remove(loadingIndicator);
                governmentIdPreview.setImage(governmentIdImage);
                viewGovernmentID.setCursor(Cursor.HAND);
                governmentIdPreview.setVisible(true);
                governmentIdPreview.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> {
                governmentIdPreviewContainer.getChildren().remove(loadingIndicator);

            });
            governmentIdPreview.setVisible(true);
            governmentIdPreview.setManaged(true);

            System.err.println("Error loading image");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }


    private void setupViewImage() {
        ImageUtils.setRoundedClip(profilePicture, 25, 25);
    }

    private void setupEventListener() {
        purposeContainer.setManaged(false);
        purposeContainer.setVisible(false);

        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateData());

        indigencyCheckBox.setOnAction(_ -> {
            if (indigencyCheckBox.isSelected()) {
                purposeContainer.setManaged(true);
                purposeContainer.setVisible(true);
            } else {
                purposeContainer.setManaged(false);
                purposeContainer.setVisible(false);
            }
        });
        residentIdInput.setOnKeyPressed(_ -> {
            residentIdInput.textProperty().addListener((_, _, newValue) -> {
                searchDelay.setOnFinished(_ -> populateInputFields(newValue));
                searchDelay.playFromStart();
            });
        });
        validator.setupResidentIdInput(residentIdInput);

        purposeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z ]*")) {
                purposeInput.setText(newValue.replaceAll("[^a-zA-Z ]", ""));
            }
            if (purposeInput.getText().length() > 40) {
                purposeInput.setText(oldValue);
            }
        });
        purposeInput.setOnKeyTyped(_ -> {
            purposeInput.getStyleClass().remove("error");
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
