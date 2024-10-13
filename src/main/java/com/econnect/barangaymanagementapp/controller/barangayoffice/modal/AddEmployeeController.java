package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

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
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
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
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.type.EmploymentType.FULL_TIME;
import static com.econnect.barangaymanagementapp.enumeration.type.EmploymentType.VOLUNTEER;
import static com.econnect.barangaymanagementapp.enumeration.type.FileType.NBI_CLEARANCE;
import static com.econnect.barangaymanagementapp.enumeration.type.FileType.RESUME;

public class AddEmployeeController {
    private final ModalUtils modalUtils;
    private final EmployeeService employeeService;
    private final ResidentService residentService;
    private final ImageService imageService;
    private Stage currentStage;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView closeBtn;

    @FXML
    private TextField residentIdInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField middleNameInput;

    @FXML
    private TextField addressInput;

    @FXML
    private TextField birthdateInput;

    @FXML
    private ComboBox<String> volunteerComboBox;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField phoneInput;

    @FXML
    private StackPane profileContainer;

    @FXML
    private ImageView profilePreview;

    @FXML
    private HBox uploadResume;

    @FXML
    private HBox viewResumeBtn;

    @FXML
    private ImageView resumePreview;

    @FXML
    private Label resumeLabel;

    @FXML
    private HBox uploadClearance;

    @FXML
    private Label clearanceLabel;

    @FXML
    private HBox viewClearanceBtn;

    @FXML
    private ImageView clearancePreview;

    @FXML
    private DatePicker nbiExpirationInput;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button confirmBtn;

    private File resumeFile;
    private File clearanceFile;
    private Boolean residentExists = false;
    private String profileLink;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public AddEmployeeController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.residentService = dependencyInjector.getResidentService();
        this.imageService = dependencyInjector.getImageService();
        Platform.runLater(() -> this.currentStage = (Stage) confirmBtn.getScene().getWindow());
    }

    public void initialize() {
        ImageUtils.setRoundedClip(profilePreview, 20, 20);
        ImageUtils.setRoundedClip(resumePreview, 10, 10);

        residentIdInput.setOnKeyPressed(_ -> configureResidentIdInput());
        setupActionButtons();
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
                    profileLink = residentInfo.getImage1x1URL();
                    firstNameInput.setText(residentInfo.getFirstName());
                    lastNameInput.setText(residentInfo.getLastName());
                    middleNameInput.setText(residentInfo.getMiddleName());
                    addressInput.setText(residentInfo.getAddress());
                    birthdateInput.setText(residentInfo.getBirthdate());
                    emailInput.setText(residentInfo.getEmail());
                    phoneInput.setText(residentInfo.getContact());
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), residentInfo.getImage1x1URL());
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
        if (!validateFields()) {
            return;
        }

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
                .status(EmployeeStatus.PENDING)
                .department(DepartmentType.NONE)
                .role(RoleType.NONE)
                .applicationType(ApplicationType.WALK_IN)
                .employment(volunteerComboBox.getValue().equals(VOLUNTEER.getName()) ? VOLUNTEER : FULL_TIME)
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

    private boolean validateFields() {
        if (!residentExists) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Resident does not exist");
            return false;
        }

        if (resumeFile == null) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Please upload a resume");
            return false;
        }

        if (clearanceFile == null) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Please upload an NBI Clearance");
            return false;
        }

        if (nbiExpirationInput.getValue() == null) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Please set NBI Clearance expiration date");
            return false;
        }

        if (volunteerComboBox.getValue() == null) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Please select employment type");
            return false;
        }


        return true;
    }

    private void uploadImage(HBox viewBtn, ImageView preview, Label label, FileType fileType) {
        FileChooser fileChooser = createFileChooser();

        File file = fileChooser.showOpenDialog(currentStage);

        if (file != null) {
            try {
                if (fileType.equals(RESUME)) {
                    resumeFile = file;
                } else if (fileType.equals(NBI_CLEARANCE)) {
                    clearanceFile = file;
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

    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        String userHome = System.getProperty("user.home");
        File picturesDirectory = new File(userHome, "Pictures");
        if (picturesDirectory.exists()) {
            fileChooser.setInitialDirectory(picturesDirectory);
        }
        return fileChooser;
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

    private void setupActionButtons() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> addEmployee());
        volunteerComboBox.getItems().addAll(VOLUNTEER.getName(), FULL_TIME.getName());

        profilePreview.setOnMouseClicked(_ -> showImageView(profilePreview.getImage()));
        viewResumeBtn.setOnMouseClicked(_ -> showImageView(resumePreview.getImage()));
        viewClearanceBtn.setOnMouseClicked(_ -> showImageView(clearancePreview.getImage()));

        uploadResume.setOnMouseClicked(_ -> uploadImage(viewResumeBtn, resumePreview, resumeLabel, RESUME));
        uploadClearance.setOnMouseClicked(_ -> uploadImage(viewClearanceBtn, clearancePreview, clearanceLabel, NBI_CLEARANCE));
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }

    @FXML
    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();
        });
    }


}