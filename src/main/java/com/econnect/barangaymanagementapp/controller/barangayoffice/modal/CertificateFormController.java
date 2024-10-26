package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.RequestService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FormValidator;
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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private HBox viewGovernmentID, profileContainer, purposeContainer;

    @FXML
    private TextField residentIdInput, nameInput, addressInput, emailInput, contactNumberInput, birthdateInput, occupationInput, sexInput;

    @FXML
    private TextArea purposeInput;

    @FXML
    private CheckBox clearanceCheckBox, indigencyCheckBox, cedulaCheckBox, bankCertificateCheckBox, businessPermitCheckBox, financialCheckBox, barangayIdCheckBox;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final FormValidator formValidator;
    private final RequestService requestService;
    private final ResidentService residentService;
    private final ImageService imageService;
    private Image validIdImage;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private boolean residentExists = false;

    public CertificateFormController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.requestService = dependencyInjector.getRequestService();
        this.residentService = dependencyInjector.getResidentService();
        this.imageService = dependencyInjector.getImageService();
        this.formValidator = dependencyInjector.getFormValidator();
        Platform.runLater(() -> this.currentStage = (Stage) confirmBtn.getScene().getWindow());
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
                try {
                    List<Request> requests = createRequestsFromInput();
                    for (Request request : requests) {
                        requestService.createRequest(request);
                    }
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

    private List<Request> createRequestsFromInput() {
        List<CheckBox> checkBoxes = List.of(clearanceCheckBox, indigencyCheckBox, cedulaCheckBox, bankCertificateCheckBox, businessPermitCheckBox, financialCheckBox, barangayIdCheckBox);
        List<Request> requests = new ArrayList<>();

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                Request request = new Request();
                request.setRequestorId(residentIdInput.getText());
                RequestType requestType = RequestType.fromName(checkBox.getText());

                if (checkBox.getText().equalsIgnoreCase("Certificate of Indigency")) {
                    request.setPurpose(purposeInput.getText());
                } else {
                    request.setPurpose("");
                }
                
                if (requestType != null) {
                    request.setRequestType(requestType);
                    requests.add(request);
                } else {
                    System.err.println("No matching RequestType found for: " + checkBox.getText());
                }
            }
        }

        return requests;
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

    private void validateData() {
        if (!formValidator.IS_NOT_EMPTY.test(residentIdInput.getText())) {
            residentIdInput.requestFocus();
            modalUtils.showModal(Modal.ERROR, "Empty Resident ID", "Please enter a Resident ID.");
            return;
        }

        if (!residentExists) {
            modalUtils.showModal(Modal.ERROR, "Resident Not Found", "Resident ID does not exist.");
            return;
        }

        if (!clearanceCheckBox.isSelected() && !indigencyCheckBox.isSelected() && !cedulaCheckBox.isSelected() && !bankCertificateCheckBox.isSelected() && !businessPermitCheckBox.isSelected() && !financialCheckBox.isSelected() && !barangayIdCheckBox.isSelected()) {
            modalUtils.showModal(Modal.ERROR, "No Certificate Selected", "Please select at least one certificate to be issued.");
            return;
        }

        if (indigencyCheckBox.isSelected()) {
            if (purposeInput.getText().isEmpty()) {
                modalUtils.showModal(Modal.ERROR, "Empty Purpose", "Please enter a purpose for the indigency certificate.");
                return;
            }
        }

        submitData();
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
        purposeContainer.setManaged(false);
        purposeContainer.setVisible(false);

        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateData());
        residentIdInput.setOnKeyPressed(_ -> {
            residentIdInput.textProperty().addListener((_, _, newValue) -> {
                searchDelay.setOnFinished(_ -> populateInputFields(newValue));
                searchDelay.playFromStart();
            });
        });
        formValidator.addListeners(residentIdInput, formValidator.IS_NOT_EMPTY, "Resident ID is required.");
        formValidator.addListeners(purposeInput, formValidator.IS_NOT_EMPTY, "Purpose is required for Indigency Certificate.");
        indigencyCheckBox.setOnAction(_ -> {
            if (indigencyCheckBox.isSelected()) {
                formValidator.addListeners(purposeInput, formValidator.IS_NOT_EMPTY, "Purpose is required for Indigency Certificate.");
                purposeContainer.setManaged(true);
                purposeContainer.setVisible(true);
            } else {
                formValidator.removeListener(purposeInput);
                purposeContainer.setManaged(false);
                purposeContainer.setVisible(false);
            }
        });

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
