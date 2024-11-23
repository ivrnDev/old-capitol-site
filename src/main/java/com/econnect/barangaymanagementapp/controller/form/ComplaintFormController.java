package com.econnect.barangaymanagementapp.controller.form;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.domain.Complaint;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.service.ComplaintService;
import com.econnect.barangaymanagementapp.service.ImageService;
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
import javafx.scene.control.Button;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEFAULT_PROFILE;

public class ComplaintFormController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn, complainantProfilePicture, respondentProfilePicture;
    @FXML
    private HBox respondentProfilePictureContainer, complainantProfilePictureContainer;
    @FXML
    private Button cancelBtn, confirmBtn;
    @FXML
    private TextField complainantIdInput, respondentIdInput, complainantLastNameInput, complainantFirstNameInput, complainantMiddleNameInput, complainantAddressInput,
            respondentLastNameInput, respondentFirstNameInput, respondentMiddleNameInput, respondentAddressInput;
    @FXML
    private TextArea complainInput, reliefSoughtInput;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final Validator validator;
    private final ImageService imageService;
    private final ComplaintService complaintService;
    private final ResidentService residentService;
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private Image respondentProfilePictureImage;
    private Image complainantProfilePictureImage;


    List<TextField> idFields = new ArrayList<>();

    private boolean respondentExist = false;
    private boolean complainantExist = false;

    public ComplaintFormController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.complaintService = dependencyInjector.getComplaintService();
        this.residentService = dependencyInjector.getResidentService();
        this.validator = dependencyInjector.getValidator();
        this.imageService = dependencyInjector.getImageService();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupList();
        setupViewImage();
        setupEventListener();
    }

    private void validateData() {

        if (validator.validate(complainantIdInput, Validator.VALIDATOR_TYPE.IS_EMPTY)) {
            complainantIdInput.requestFocus();
            complainantIdInput.setStyle("-fx-border-color: red;");
            modalUtils.showModal(Modal.ERROR, "Empty Resident ID", "Please enter a Resident ID.");
            return;
        } else {
            complainantIdInput.setStyle(null);
        }

        if (validator.validate(respondentIdInput, Validator.VALIDATOR_TYPE.IS_EMPTY)) {
            respondentIdInput.requestFocus();
            respondentIdInput.setStyle("-fx-border-color: red;");
            modalUtils.showModal(Modal.ERROR, "Empty Respondent's ID", "Please enter a Respondent ID.");
            return;
        } else {
            respondentIdInput.setStyle(null);
        }

        if (!complainantExist) {
            modalUtils.showModal(Modal.ERROR, "Resident Not Found", "Resident ID does not exist.");
            return;
        }

        if (!respondentExist) {
            modalUtils.showModal(Modal.ERROR, "Resident Not Found", "Respondent ID does not exist.");
            return;
        }

        if (respondentIdInput.getText().equals(complainantIdInput.getText())) {
            modalUtils.showModal(Modal.ERROR, "Mismatch Error", "Complainant ID and Respondent ID cannot be the same.");
            return;
        }

        if (validator.hasEmptyFields(List.of(reliefSoughtInput, complainInput))) return;

        submitData();
    }

    private void submitData() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Task<Response> addComplaintTask = new Task<>() {
            @Override
            protected Response call() {
                Complaint complaint = createComplaintFromInput();
                return complaintService.createComplaint(complaint);
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                closeWindow();

                if (getValue().isSuccessful())
                    modalUtils.showModal(Modal.SUCCESS, "Success", "Complaint Request has been successfully submitted.");
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while submitting request.");
            }
        };

        new Thread(addComplaintTask).start();
    }

    private Complaint createComplaintFromInput() {
        return Complaint.builder()
                .id(respondentIdInput.getText())
                .respondentId(respondentIdInput.getText())
                .respondentName(respondentFirstNameInput.getText() + " " + respondentMiddleNameInput.getText() + " " + respondentLastNameInput.getText())
                .respondentAddress(respondentAddressInput.getText())
                .problem(reliefSoughtInput.getText())
                .solution(complainInput.getText())
                .build();
    }

    private void populateInputFields(String residentId, TextField inputField) {
        if (residentId.isEmpty()) {
            clearInputFields(inputField);
            return;
        }

        Task<Optional<Resident>> fetchResident = new Task<>() {
            @Override
            protected Optional<Resident> call() {
                return residentService.findVerifiedResidentById(residentId);
            }

            @Override
            protected void succeeded() {
                Optional<Resident> resident = getValue();
                if (resident.isEmpty()) {
                    clearInputFields(inputField);
                    return;
                }
                Resident residentInfo = resident.get();

                if (inputField.equals(complainantIdInput)) {
                    complainantExist = true;
                    complainantLastNameInput.setText(residentInfo.getLastName());
                    complainantFirstNameInput.setText(residentInfo.getFirstName());
                    complainantMiddleNameInput.setText(residentInfo.getMiddleName());
                    complainantAddressInput.setText(residentInfo.getAddress());
                    loadComplainantImage(Firestore.VALID_ID.getPath(), residentInfo.getProfileUrl());
                } else {
                    respondentExist = true;
                    respondentLastNameInput.setText(residentInfo.getLastName());
                    respondentFirstNameInput.setText(residentInfo.getFirstName());
                    respondentMiddleNameInput.setText(residentInfo.getMiddleName());
                    respondentAddressInput.setText(residentInfo.getAddress());
                    loadRespondentImage(Firestore.VALID_ID.getPath(), residentInfo.getProfileUrl());
                }

            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };
        new Thread(fetchResident).start();
    }

    private void loadComplainantImage(String directory, String link) {
        complainantProfilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(complainantProfilePicture.getImage(), currentStage));
        complainantProfilePicture.setCursor(Cursor.HAND);
        complainantProfilePicture.setVisible(false);
        complainantProfilePicture.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(complainantProfilePictureContainer.getWidth(), complainantProfilePictureContainer.getHeight());
        Platform.runLater(() -> complainantProfilePictureContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            complainantProfilePictureImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                complainantProfilePictureContainer.getChildren().remove(loadingIndicator);
                complainantProfilePicture.setImage(complainantProfilePictureImage);
                complainantProfilePicture.setVisible(true);
                complainantProfilePicture.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> complainantProfilePictureContainer.getChildren().remove(loadingIndicator));
            complainantProfilePicture.setVisible(true);
            complainantProfilePicture.setManaged(true);
            System.err.println("Error loading employees");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void loadRespondentImage(String directory, String link) {
        respondentProfilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(respondentProfilePicture.getImage(), currentStage));
        respondentProfilePicture.setCursor(Cursor.HAND);
        respondentProfilePicture.setVisible(false);
        respondentProfilePicture.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(respondentProfilePictureContainer.getWidth(), respondentProfilePictureContainer.getHeight());
        Platform.runLater(() -> respondentProfilePictureContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            respondentProfilePictureImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                respondentProfilePictureContainer.getChildren().remove(loadingIndicator);
                respondentProfilePicture.setImage(respondentProfilePictureImage);
                respondentProfilePicture.setVisible(true);
                respondentProfilePicture.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> respondentProfilePictureContainer.getChildren().remove(loadingIndicator));
            respondentProfilePicture.setVisible(true);
            respondentProfilePicture.setManaged(true);
            System.err.println("Error loading employees");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void clearInputFields(TextField inputField) {

        if (inputField.equals(complainantIdInput)) {
            complainantLastNameInput.clear();
            complainantFirstNameInput.clear();
            complainantMiddleNameInput.clear();
            complainantAddressInput.clear();
            complainantProfilePicture.setImage(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath()))));
        } else {
            respondentLastNameInput.clear();
            respondentFirstNameInput.clear();
            respondentMiddleNameInput.clear();
            respondentAddressInput.clear();
            respondentProfilePicture.setImage(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath()))));
        }
    }

    private void setupEventListener() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateData());

        idFields.forEach(field -> {
            field.setOnKeyPressed(_ -> {
                field.textProperty().addListener((_, _, newValue) -> {
                    searchDelay.setOnFinished(_ -> populateInputFields(newValue, field));
                    searchDelay.playFromStart();
                });
            });
            validator.setupResidentIdInput(field);
        });
    }

    private void setupList() {
        idFields = new ArrayList<>(List.of(complainantIdInput, respondentIdInput));
    }

    private void setupViewImage() {
        ImageUtils.setRoundedClip(complainantProfilePicture, 25, 25);
        ImageUtils.setRoundedClip(respondentProfilePicture, 25, 25);
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
