package com.econnect.barangaymanagementapp.controller.shared.modal;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.FileType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.Validator;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.FileChooserUtils;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEFAULT_DOCUMENT;
import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEFAULT_PROFILE;
import static com.econnect.barangaymanagementapp.enumeration.type.EmploymentType.FULL_TIME;
import static com.econnect.barangaymanagementapp.enumeration.type.EmploymentType.VOLUNTEER;
import static com.econnect.barangaymanagementapp.enumeration.type.FileType.NBI_CLEARANCE;
import static com.econnect.barangaymanagementapp.enumeration.type.FileType.RESUME;

public class AddEmployeeController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn, profilePicture, governmentIdPreview, clearancePreview, resumePreview;
    @FXML
    private TextField residentIdInput, firstNameInput, lastNameInput, middleNameInput, addressInput, birthdateInput, emailInput, contactNumberInput;
    @FXML
    private ComboBox<String> employmentTypeComboBox;
    @FXML
    private HBox uploadResume, uploadClearance, profileContainer, viewGovernmentID, viewClearance, viewResume, governmentIdPreviewContainer;
    @FXML
    private Label resumeLabel, clearanceLabel;
    @FXML
    private DatePicker nbiExpirationPicker;
    @FXML
    private Button cancelBtn, confirmBtn;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final EmployeeService employeeService;
    private final ResidentService residentService;
    private final ImageService imageService;
    private final FileChooserUtils fileChooserUtils;
    private final Validator validator;
    private File resumeFile;
    private File clearanceFile;
    private Image profilePictureImage;
    private Image governmentIdImage;
    private String profileLink;
    private Boolean residentExists = false;
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public AddEmployeeController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.residentService = dependencyInjector.getResidentService();
        this.imageService = dependencyInjector.getImageService();
        this.fileChooserUtils = dependencyInjector.getFileChooserUtils();
        this.validator = dependencyInjector.getValidator();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupPreviewRounded();
        setupEventListeners();
        residentIdInput.setOnKeyPressed(_ -> configureResidentIdInput());
    }

    private void configureResidentIdInput() {
        residentIdInput.textProperty().addListener((_, _, newValue) -> {
            searchDelay.setOnFinished(event -> populateInputFields(newValue));
            searchDelay.playFromStart();
        });
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
                    profileLink = residentInfo.getProfileUrl();
                    firstNameInput.setText(residentInfo.getFirstName());
                    lastNameInput.setText(residentInfo.getLastName());
                    middleNameInput.setText(residentInfo.getMiddleName());
                    addressInput.setText(residentInfo.getAddress());
                    birthdateInput.setText(residentInfo.getBirthdate().toString());
                    emailInput.setText(residentInfo.getEmail());
                    contactNumberInput.setText(residentInfo.getMobileNumber());
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), residentInfo.getProfileUrl());
                    loadGovernmentIdImage(Firestore.VALID_ID.getPath(), residentInfo.getValidIdUrl());
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

        residentExists = false;
        firstNameInput.clear();
        lastNameInput.clear();
        middleNameInput.clear();
        addressInput.clear();
        birthdateInput.clear();
        emailInput.clear();
        contactNumberInput.clear();
    }

    private void addEmployee() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Employee employee = createEmployeeFromInputs();

        Task<Void> addEmployeeTask = new Task<>() {
            @Override
            protected Void call() {
                return processEmployeeCreation(employee);
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                closeWindow();
                modalUtils.showModal(Modal.SUCCESS, "Success", "Employee application has been submitted successfully.");
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while adding the employee.");
            }
        };

        new Thread(addEmployeeTask).start();
    }

    private Employee createEmployeeFromInputs() {
        return Employee.builder()
                .id(residentIdInput.getText())
                .firstName(firstNameInput.getText())
                .lastName(lastNameInput.getText())
                .middleName(middleNameInput.getText())
                .address(addressInput.getText())
                .email(emailInput.getText())
                .contactNumber(contactNumberInput.getText())
                .createdAt(DateFormatter.convertToUtc(ZonedDateTime.now()))
                .updatedAt(DateFormatter.convertToUtc(ZonedDateTime.now()))
                .status(EmployeeStatus.UNDER_REVIEW)
                .department(DepartmentType.NONE)
                .role(RoleType.NONE)
                .applicationType(ApplicationType.WALK_IN)
                .employment(employmentTypeComboBox.getValue().equals(VOLUNTEER.getName()) ? VOLUNTEER : FULL_TIME)
                .nbiClearanceExpiration(DateFormatter.formatLocalDateToUsShortDate(nbiExpirationPicker.getValue()))
                .build();
    }

    private Void processEmployeeCreation(Employee employee) {
        String resumeUrl = imageService.uploadImage(Firestore.RESUME, resumeFile, employee.getId());
        String nbiClearanceUrl = imageService.uploadImage(Firestore.NBI_CLEARANCE, clearanceFile, employee.getId());
        employee.setResumeUrl(resumeUrl);
        employee.setProfileUrl(profileLink);
        employee.setNbiClearanceUrl(nbiClearanceUrl);
        employeeService.createEmployee(employee);
        return null;
    }

    private void validateFields() {
        File[] files = {resumeFile, clearanceFile};
        HBox[] uploadBtns = {uploadResume, uploadClearance};
        DatePicker[] datePickers = {nbiExpirationPicker};
        ComboBox[] comboBoxes = {employmentTypeComboBox};

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

        if (validator.hasEmptyFields((TextField[]) null, datePickers, comboBoxes)) return;

        if (validator.hasEmptyFiles(files, uploadBtns)) return;

        addEmployee();
    }

    private void uploadImage(HBox viewBtn, ImageView preview, Label label, FileType fileType) {
        FileChooser fileChooser = fileChooserUtils.createFileChooser();
        File file = fileChooser.showOpenDialog(currentStage);

        if (file != null) {
            try {
                if (fileType.equals(RESUME)) {
                    resumeFile = file;
                    viewResume.setCursor(Cursor.HAND);
                    validator.hasEmptyFile(resumeFile, uploadResume);
                } else if (fileType.equals(NBI_CLEARANCE)) {
                    clearanceFile = file;
                    viewClearance.setCursor(Cursor.HAND);
                    validator.hasEmptyFile(clearanceFile, uploadClearance);
                }

                preview.setImage(new Image(new FileInputStream(file)));
                label.setText(file.getName());
                viewBtn.setVisible(true);

            } catch (FileNotFoundException e) {
                Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Error", "File not found: " + file.getName()));
                e.printStackTrace();
            }
        } else {
            viewClearance.setCursor(Cursor.HAND);
            viewResume.setCursor(Cursor.HAND);
            Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "No File Selected", "Please select a valid file."));
        }
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
            Platform.runLater(() -> {
                profileContainer.getChildren().remove(loadingIndicator);
                profilePicture.setVisible(true);
                profilePicture.setManaged(true);
            });
            System.err.println("Error loading profile image");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void loadGovernmentIdImage(String directory, String link) {
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

    private void showImageView(Image image) {
        modalUtils.showImageView(image, currentStage);
    }

    private void setupEventListeners() {
        LocalDate minDate = LocalDate.now().plusMonths(3);
        LocalDate maxDate = LocalDate.now().plusYears(1);

        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateFields());
        employmentTypeComboBox.getItems().addAll(VOLUNTEER.getName(), FULL_TIME.getName());

        viewResume.setOnMouseClicked(_ -> {
            if (resumeFile != null) showImageView(resumePreview.getImage());
        });

        viewClearance.setOnMouseClicked(_ -> {
            if (clearanceFile != null) showImageView(clearancePreview.getImage());
        });

        uploadResume.setOnMouseClicked(_ -> uploadImage(viewResume, resumePreview, resumeLabel, RESUME));
        uploadClearance.setOnMouseClicked(_ -> uploadImage(viewClearance, clearancePreview, clearanceLabel, NBI_CLEARANCE));

        validator.setupDatePicker(minDate, maxDate, nbiExpirationPicker);
        validator.setupResidentIdInput(residentIdInput);
        validator.setupComboBox(employmentTypeComboBox);
    }

    private void setupPreviewRounded() {
        ImageUtils.setRoundedClip(profilePicture, 25, 25);
        ImageUtils.setRoundedClip(resumePreview, 10, 10);
        ImageUtils.setRoundedClip(clearancePreview, 10, 10);
        ImageUtils.setRoundedClip(governmentIdPreview, 10, 10);
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