package com.econnect.barangaymanagementapp.controller.shared.modal;

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
import com.econnect.barangaymanagementapp.util.FormValidator;
import com.econnect.barangaymanagementapp.util.Validator;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.FileChooserUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
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
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.type.EmploymentType.FULL_TIME;
import static com.econnect.barangaymanagementapp.enumeration.type.EmploymentType.VOLUNTEER;
import static com.econnect.barangaymanagementapp.enumeration.type.FileType.NBI_CLEARANCE;
import static com.econnect.barangaymanagementapp.enumeration.type.FileType.RESUME;

public class AddEmployeeController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn, profilePreview, resumePreview, clearancePreview;
    @FXML
    private TextField residentIdInput, lastNameInput, firstNameInput, middleNameInput, addressInput, birthdateInput, emailInput, phoneInput;
    @FXML
    private ComboBox<String> employmentComboBox;
    @FXML
    private HBox uploadResume, viewResumeBtn, uploadClearance, viewClearanceBtn;
    @FXML
    private Label resumeLabel, clearanceLabel;
    @FXML
    private DatePicker nbiExpirationInput;
    @FXML
    private StackPane profileContainer;
    @FXML
    private Button cancelBtn, confirmBtn;

    private final ModalUtils modalUtils;
    private final EmployeeService employeeService;
    private final ResidentService residentService;
    private final ImageService imageService;
    private final FileChooserUtils fileChooserUtils;
    private final Validator validator;
    private Stage currentStage;
    private File resumeFile;
    private File clearanceFile;
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
        Platform.runLater(() -> this.currentStage = (Stage) confirmBtn.getScene().getWindow());
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
                    phoneInput.setText(residentInfo.getMobileNumber());
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), residentInfo.getProfileUrl());
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
        profilePreview.setVisible(false);
        profilePreview.setImage(null);
        firstNameInput.clear();
        lastNameInput.clear();
        middleNameInput.clear();
        addressInput.clear();
        birthdateInput.clear();
        emailInput.clear();
        phoneInput.clear();
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
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while adding the employee."));
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
                .contactNumber(phoneInput.getText())
                .createdAt(DateFormatter.getFormattedZonedDateTime(ZonedDateTime.now()))
                .updatedAt(DateFormatter.getFormattedZonedDateTime(ZonedDateTime.now()))
                .status(EmployeeStatus.UNDER_REVIEW)
                .department(DepartmentType.NONE)
                .role(RoleType.NONE)
                .applicationType(ApplicationType.WALK_IN)
                .employment(employmentComboBox.getValue().equals(VOLUNTEER.getName()) ? VOLUNTEER : FULL_TIME)
                .nbiClearanceExpiration(String.valueOf(nbiExpirationInput.getValue()))
                .build();
    }

    private Void processEmployeeCreation(Employee employee) {
        String resumeUrl = imageService.uploadImage(Firestore.RESUME, resumeFile, employee.getId());
        String nbiClearanceUrl = imageService.uploadImage(Firestore.NBI_CLEARANCE, clearanceFile, employee.getId());
        employee.setResumeUrl(resumeUrl);
        employee.setProfileUrl(profileLink);
        employee.setNbiClearanceUrl(nbiClearanceUrl);

        try (Response response = employeeService.createEmployee(employee)) {
            if (response.isSuccessful()) {
                Platform.runLater(() -> modalUtils.showModal(Modal.SUCCESS, "Success", "Employee added successfully"));
            } else {
                Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Failed", "Failed to add employee"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void validateFields() {
        File[] files = {resumeFile, clearanceFile};
        HBox[] uploadBtns = {uploadResume, uploadClearance};
        DatePicker[] datePickers = {nbiExpirationInput};
        ComboBox[] comboBoxes = {employmentComboBox};

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

        if (validator.hasEmptyFields(null, datePickers, comboBoxes)) return;

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
                    validator.hasEmptyFile(resumeFile, uploadResume);
                } else if (fileType.equals(NBI_CLEARANCE)) {
                    clearanceFile = file;
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
            Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "No File Selected", "Please select a valid file."));
        }
    }

    private void loadProfileImage(String directory, String link) {
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(true);
        profileContainer.getChildren().add(loadingIndicator);

        Task<Image> imageTask = new Task<>() {
            @Override
            protected Image call() {
                return imageService.getImage(directory, link);
            }

            @Override
            protected void succeeded() {
                Image image = getValue();
                loadingIndicator.setVisible(false);
                profilePreview.setVisible(true);
                profilePreview.setImage(image);
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                profilePreview.setVisible(false);
                System.out.println("Image loading failed: " + getException().getMessage());
            }
        };

        new Thread(imageTask).start();
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
        employmentComboBox.getItems().addAll(VOLUNTEER.getName(), FULL_TIME.getName());

        profilePreview.setOnMouseClicked(_ -> showImageView(profilePreview.getImage()));
        viewResumeBtn.setOnMouseClicked(_ -> showImageView(resumePreview.getImage()));
        viewClearanceBtn.setOnMouseClicked(_ -> showImageView(clearancePreview.getImage()));

        uploadResume.setOnMouseClicked(_ -> uploadImage(viewResumeBtn, resumePreview, resumeLabel, RESUME));
        uploadClearance.setOnMouseClicked(_ -> uploadImage(viewClearanceBtn, clearancePreview, clearanceLabel, NBI_CLEARANCE));

        validator.setupDatePicker(minDate, maxDate, nbiExpirationInput);
        validator.setupResidentIdInput(residentIdInput);
        validator.setupComboBox(employmentComboBox);
    }

    private void setupPreviewRounded() {
        ImageUtils.setRoundedClip(profilePreview, 20, 20);
        ImageUtils.setRoundedClip(resumePreview, 10, 10);
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