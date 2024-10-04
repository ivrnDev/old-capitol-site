package com.econnect.barangaymanagementapp.Controller.HumanResources.Modal;

import com.econnect.barangaymanagementapp.Domain.Resident;
import com.econnect.barangaymanagementapp.Enumeration.Gender;
import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.Enumeration.Paths.ImageDirectory;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Service.ImageService;
import com.econnect.barangaymanagementapp.Service.ResidentService;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.ImageUtils;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.Enumeration.EmployeeType.*;

public class AddEmployeeController {
    private final ModalUtils modalUtils;
    private final EmployeeService employeeService;
    private final ResidentService residentService;
    private final ImageService imageService;
    private Stage currentStage;
    private Image resumeImage;

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
    private RadioButton maleBtn;

    @FXML
    private RadioButton femaleBtn;

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

        residentIdInput.setOnKeyPressed(_ -> setupResidentIdInput());
        setupActionButtons();
    }

    private void setupResidentIdInput() {
        residentIdInput.textProperty().addListener((_, _, newValue) -> {
            if (debounceTimeline != null) {
                debounceTimeline.stop();
            }

            debounceTimeline = new Timeline(new KeyFrame(Duration.millis(300), event -> setupInputFields(newValue)));
            debounceTimeline.play();
        });
    }

    private void setupInputFields(String residentId) {
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
                    Resident residentInfo = resident.get();
                    firstNameInput.setText(residentInfo.getFirstName());
                    lastNameInput.setText(residentInfo.getLastName());
                    middleNameInput.setText(residentInfo.getMiddleName());
                    addressInput.setText(residentInfo.getAddress());
                    birthdateInput.setText(residentInfo.getBirthdate());
                    emailInput.setText(residentInfo.getEmail());
                    phoneInput.setText(residentInfo.getContact());
                    if (residentInfo.getGender().equals(Gender.MALE)) {
                        maleBtn.setSelected(true);
                    } else {
                        femaleBtn.setSelected(true);
                    }
                    loadProfileImageAsync(ImageDirectory.PROFILE_PICTURE.getPath(), residentInfo.getImage1x1URL());
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
        closeWindow();
//        var employee = new Employee(
//                "909090",                           // id
//                "John",                            // firstName
//                "Doe",                             // lastName
//                "Software Engineer",               // position
//                "johndoe@example.com",             // email
//                "123-456-7890",                    // contactNumber
//                "123 Main St, Anytown, USA",       // address
//                Gender.MALE,                       // gender
//                Roles.HR_MANAGER,                       // role
//                " ",                         // username
//                " ",                     // access
//                Status.EmployeeStatus.ACTIVE,             // status
//                Departments.HUMAN_RESOURCES,                    // department
//                LocalDateTime.now(),               // createdAt
//                LocalDateTime.now(),               // updatedAt
//                LocalDateTime.now().minusDays(1)   // lastLogin
//        );

//        Response response = employeeService.createEmployee(employee);
//        if (response.isSuccessful()) {
//            modalUtils.showModal(Modal.SUCCESS, "Success", "Employee added successfully");
//        }
//        else {
        modalUtils.showModal(Modal.SUCCESS, "Success", "Employee added successfully");
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        String userHome = System.getProperty("user.home");
        File picturesDirectory = new File(userHome, "Pictures");
        if (picturesDirectory.exists()) {
            fileChooser.setInitialDirectory(picturesDirectory);
        }

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

    private void loadProfileImageAsync(String directory, String link) {
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
        volunteerComboBox.getItems().addAll(VOLUNTEER.getName(), FULL_TIME.getName(), PART_TIME.getName());
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