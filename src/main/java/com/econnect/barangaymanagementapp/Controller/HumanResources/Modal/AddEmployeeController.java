package com.econnect.barangaymanagementapp.Controller.HumanResources.Modal;

import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Service.ImageService;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AddEmployeeController {
    private final ModalUtils modalUtils;
    private final EmployeeService employeeService;
    private final ImageService imageService;
    private Stage currentStage;

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
    private ComboBox<String> volunteeComboBox;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField phoneInput;

    @FXML
    private ImageView profilePreview;

    @FXML
    private HBox viewResume;
    @FXML
    private ImageView resumePreview;

    @FXML
    private Label resumeLabel;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button confirmBtn;

    public AddEmployeeController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.imageService = dependencyInjector.getImageService();
        Platform.runLater(() -> {
            if (confirmBtn != null) {
                this.currentStage = (Stage) confirmBtn.getScene().getWindow();
            }
        });
    }

    public void initialize() {
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> addEmployee());
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

        File file = fileChooser.showOpenDialog(currentStage);

        try {
            if (file != null) {
                resumePreview.setImage(new Image(new FileInputStream(file)));
                resumeLabel.setText(file.getName());
                viewResume.setVisible(true);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void showResume() {
        modalUtils.showImageView(resumePreview.getImage(), currentStage);
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