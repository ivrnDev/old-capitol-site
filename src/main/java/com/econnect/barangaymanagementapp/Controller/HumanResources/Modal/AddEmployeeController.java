package com.econnect.barangaymanagementapp.Controller.HumanResources.Modal;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Domain.Resident;
import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.Enumeration.Paths.ImageDirectory;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Service.ImageService;
import com.econnect.barangaymanagementapp.Service.ResidentService;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.ImageUtils;
import com.econnect.barangaymanagementapp.Utils.LoadingIndicator;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.Enumeration.EmploymentType.*;

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
    private HBox viewResumeBtn;

    @FXML
    private ImageView resumePreview;

    @FXML
    private Label resumeLabel;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button confirmBtn;

    private File file;
    private Timeline debounceTimeline;
    private Boolean residentExists = false;

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
            if (debounceTimeline != null) {
                debounceTimeline.stop();
            }

            debounceTimeline = new Timeline(new KeyFrame(Duration.millis(300), event -> populateInputFields(newValue)));
            debounceTimeline.play();
        });
    }

    private void populateInputFields(String residentId) {
        if (residentId.isEmpty()) {
            clearInputFields();
            return;
        }

        Task<Optional<Resident>> residentTask = new Task<>() {
            @Override
            protected Optional<Resident> call() throws Exception {
                return residentService.findResidentById(residentId);
            }

            @Override
            protected void succeeded() {
                Optional<Resident> resident = getValue();
                if (resident.isPresent()) {
                    residentExists = true;
                    Resident residentInfo = resident.get();
                    firstNameInput.setText(residentInfo.getFirstName());
                    lastNameInput.setText(residentInfo.getLastName());
                    middleNameInput.setText(residentInfo.getMiddleName());
                    addressInput.setText(residentInfo.getAddress());
                    birthdateInput.setText(residentInfo.getBirthdate());
                    emailInput.setText(residentInfo.getEmail());
                    phoneInput.setText(residentInfo.getContact());
                    loadProfileImage(ImageDirectory.PROFILE_PICTURE.getPath(), residentInfo.getImage1x1URL());
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
        if (!validateEmployeeFields()) {
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
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Void processEmployeeCreation(Employee employee) {
        Response response = employeeService.createEmployee(employee);
        imageService.uploadImage(ImageDirectory.RESUME, file, employee.getId());

        if (response.isSuccessful()) {
            Platform.runLater(() -> modalUtils.showModal(Modal.SUCCESS, "Success", "Employee added successfully"));
        } else {
            Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Failed", "Failed to add employee"));
        }
        return null;
    }

    private boolean validateEmployeeFields() {
        if (!residentExists) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Resident does not exist");
            return false;
        }

        if (volunteerComboBox.getValue() == null) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Please select employment type");
            return false;
        }

        if (file == null) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Please upload a resume");
            return false;
        }
        return true;
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = createFileChooser();
        file = fileChooser.showOpenDialog(currentStage);

        try {
            if (file != null) {
                resumePreview.setImage(new Image(new FileInputStream(file)));
                resumeLabel.setText(file.getName());
                viewResumeBtn.setVisible(true);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
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
                profilePreview.setImage(image != null ? image : null);
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
        closeBtn.setOnMouseClicked(event -> closeWindowConfirmation());
        cancelBtn.setOnAction(event -> closeWindowConfirmation());
        confirmBtn.setOnAction(event -> addEmployee());
        viewResumeBtn.setOnMouseClicked(_ -> showImageView(resumePreview.getImage()));
        profilePreview.setOnMouseClicked(_ -> showImageView(profilePreview.getImage()));
        volunteerComboBox.getItems().addAll(VOLUNTEER.getName(), FULL_TIME.getName());
    }

    @FXML
    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();
        });
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }
}