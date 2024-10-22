package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.FileType;
import com.econnect.barangaymanagementapp.enumeration.type.GenderType;
import com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FormValidator;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.FileChooserUtils;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.econnect.barangaymanagementapp.enumeration.type.FileType.GOVERNMENT_ID;
import static com.econnect.barangaymanagementapp.enumeration.type.FileType.PROFILE_PICTURE;

public class AddResidentController {
    private final ModalUtils modalUtils;
    private final ImageService imageService;
    private final FileChooserUtils fileChooserUtils;
    private final FormValidator formValidator;
    private Stage currentStage;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField residentIdInput, lastNameInput, firstNameInput, middleNameInput, birthplaceInput, occupationInput, emailInput, addressInput, contactNumberInput, fatherFirstNameInput, fatherLastNameInput, fatherMiddleNameInput, fatherOccupationInput, motherFirstNameInput, motherLastNameInput, motherMiddleNameInput, motherOccupationInput, citizenshipInput;
    @FXML
    private ComboBox<String> suffixInput, sexComboBox, civilStatusComboBox, motherToungeComboBox, religionComboBox, bloodTypeComboBox, fatherSuffixNameComboBox, motherSuffixComboBox;
    @FXML
    private DatePicker birthdatePicker, fatherBirthdatePicker, motherBirthdatePicker;
    @FXML
    private HBox uploadProfile, viewProfileBtn, uploadGovernmentId, viewGovernmentIdBtn;
    @FXML
    private Label profileLabel, governmentIdLabel;
    @FXML
    private ImageView profilePreview, governmentIdPreview, closeBtn;
    @FXML
    private Button cancelBtn, confirmBtn;

    private File profileFile, governmentIdFile;
    private final LocalDate minimumDate = LocalDate.now().minusYears(120);
    private final LocalDate maximumDate = LocalDate.now().minusYears(12);
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public AddResidentController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.imageService = dependencyInjector.getImageService();
        this.fileChooserUtils = dependencyInjector.getFileChooserUtils();
        this.formValidator = dependencyInjector.getFormValidator();
        Platform.runLater(() -> this.currentStage = (Stage) confirmBtn.getScene().getWindow());
    }

    public void initialize() {
        setPreviewRounded();
        setupActionButtons();
        setupInputFields();
        mockData();
    }

    private void setupInputFields() {
        populateComboBoxes();
        setupDatePicker();
        citizenshipInput.setEditable(false);
        citizenshipInput.setText("Filipino");
    }

//    private void addResident() {
//        if (!validateFields()) {
//            return;
//        }
//
//        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
//        rootPane.getChildren().add(loadingIndicator);
//
//        Employee employee = createEmployeeFromInputs();
//
//        Task<Void> addEmployeeTask = new Task<>() {
//            @Override
//            protected Void call() {
//                return processEmployeeCreation(employee);
//            }
//
//            @Override
//            protected void succeeded() {
//                loadingIndicator.setVisible(false);
//                rootPane.getChildren().remove(loadingIndicator);
//                closeWindow();
//            }
//
//            @Override
//            protected void failed() {
//                loadingIndicator.setVisible(false);
//                rootPane.getChildren().remove(loadingIndicator);
//                Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while adding the employee."));
//            }
//        };
//
//        new Thread(addEmployeeTask).start();
//    }

//    private Void processResidentCreation(Employee employee) {
//        String resumeUrl = imageService.uploadImage(Firestore.RESUME, resumeFile, employee.getId());
//        String nbiClearanceUrl = imageService.uploadImage(Firestore.NBI_CLEARANCE, clearanceFile, employee.getId());
//        employee.setResumeUrl(resumeUrl);
//        employee.setProfileUrl(profileLink);
//        employee.setNbiClearanceUrl(nbiClearanceUrl);
//
//
//        try (Response response = employeeService.createEmployee(employee)) {
//            if (response.isSuccessful()) {
//                Platform.runLater(() -> modalUtils.showModal(Modal.SUCCESS, "Success", "Employee added successfully"));
//            } else {
//                Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Failed", "Failed to add employee"));
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return null;
//    }

    private void uploadImage(HBox viewBtn, ImageView preview, Label label, FileType fileType) {
        FileChooser fileChooser = fileChooserUtils.createFileChooser();
        File file = fileChooser.showOpenDialog(currentStage);

        if (file != null) {
            try {
                if (fileType.equals(PROFILE_PICTURE)) {
                    profileFile = file;
                    formValidator.validateFile(profileFile, uploadProfile);
                } else if (fileType.equals(GOVERNMENT_ID)) {
                    governmentIdFile = file;
                    formValidator.validateFile(governmentIdFile, uploadGovernmentId);
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

    public void mockData() {
        residentIdInput.setText("RES-0001");
        lastNameInput.setText("Dela Cruz");
        firstNameInput.setText("Juan");
        middleNameInput.setText("Santos");
        suffixInput.setValue("Jr.");
        birthplaceInput.setText("dsa");
        occupationInput.setText("Software Engineer");
        emailInput.setText("");
        addressInput.setText("1234 Barangay St., Barangay, City");
        contactNumberInput.setText("09123456789");
        fatherFirstNameInput.setText("Juan");
        fatherLastNameInput.setText("Dela Cruz");
        fatherMiddleNameInput.setText("Santos");
        fatherSuffixNameComboBox.setValue("Jr.");
        fatherOccupationInput.setText("Software Engineer");
        motherFirstNameInput.setText("Maria");
        motherLastNameInput.setText("Dela Cruz");
        motherMiddleNameInput.setText("Santos");
        motherSuffixComboBox.setValue("Jr.");
        motherOccupationInput.setText("Software Engineer");
        citizenshipInput.setText("Filipino");
        suffixInput.setValue("Jr.");
        suffixInput.setValue("Jr.");
        sexComboBox.setValue("Male");
        civilStatusComboBox.setValue("Single");
        motherToungeComboBox.setValue("Tagalog");
        religionComboBox.setValue("Catholicism");
        bloodTypeComboBox.setValue("O+");
        fatherSuffixNameComboBox.setValue("Sr.");
        motherSuffixComboBox.setValue("Jr.");

    }

    private void setupActionButtons() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateForm());

        viewProfileBtn.setOnMouseClicked(_ -> showImageView(profilePreview.getImage()));
        viewGovernmentIdBtn.setOnMouseClicked(_ -> showImageView(governmentIdPreview.getImage()));

        uploadProfile.setOnMouseClicked(_ -> uploadImage(viewProfileBtn, profilePreview, profileLabel, PROFILE_PICTURE));
        uploadGovernmentId.setOnMouseClicked(_ -> uploadImage(viewGovernmentIdBtn, governmentIdPreview, governmentIdLabel, GOVERNMENT_ID));
    }

    private void validateForm() {
        boolean hasEmptyInput = formValidator.hasEmptyField(lastNameInput, firstNameInput, middleNameInput, birthplaceInput, occupationInput, emailInput, addressInput, contactNumberInput, fatherFirstNameInput, fatherLastNameInput, fatherMiddleNameInput, fatherOccupationInput, motherFirstNameInput, motherLastNameInput, motherMiddleNameInput, motherOccupationInput, citizenshipInput, suffixInput, sexComboBox, civilStatusComboBox, motherToungeComboBox, religionComboBox, bloodTypeComboBox, fatherSuffixNameComboBox, motherSuffixComboBox, birthdatePicker, fatherBirthdatePicker, motherBirthdatePicker);
        boolean hasEmptyProfile = formValidator.hasEmptyFile(profileFile, uploadProfile);
        boolean hasEmptyGovernmentId = formValidator.hasEmptyFile(governmentIdFile, uploadGovernmentId);
        if (hasEmptyInput) {
            modalUtils.showModal(Modal.ERROR, "Error", "Please fill out all required fields.");
            return;
        }

        if (formValidator.validateDate(minimumDate, maximumDate, birthdatePicker, fatherBirthdatePicker, motherBirthdatePicker)) {
            return;
        }

        if (hasEmptyProfile) {
            modalUtils.showModal(Modal.ERROR, "Error", "Please upload a profile picture.");
            return;
        }
        if (hasEmptyGovernmentId) {
            modalUtils.showModal(Modal.ERROR, "Error", "Please upload a government ID.");
            return;
        }
    }

    private void populateComboBoxes() {
        List<String> suffixNamesChoices = Arrays.stream(ResidentInfomationType.SuffixName.values()).map(suffix -> suffix.getName()).toList();
        suffixInput.getItems().addAll(suffixNamesChoices);
        fatherSuffixNameComboBox.getItems().addAll(suffixNamesChoices);
        motherSuffixComboBox.getItems().addAll(suffixNamesChoices);
        civilStatusComboBox.getItems().addAll(Arrays.stream(ResidentInfomationType.CivilStatus.values()).map(civilStatus -> civilStatus.getName()).toList());
        religionComboBox.getItems().addAll(Arrays.stream(ResidentInfomationType.Religion.values()).map(religion -> religion.getName()).toList());
        bloodTypeComboBox.getItems().addAll(Arrays.stream(ResidentInfomationType.BloodType.values()).map(bloodType -> bloodType.getName()).toList());
        sexComboBox.getItems().addAll(Arrays.stream(GenderType.values()).map(gender -> gender.getName()).toList());
        motherToungeComboBox.getItems().addAll(Arrays.stream(ResidentInfomationType.MotherTongue.values()).map(motherTongue -> motherTongue.getName()).toList());
    }

    private void setupDatePicker() {
        DatePicker[] datePickers = {birthdatePicker, fatherBirthdatePicker, motherBirthdatePicker};
        formValidator.setupDatePicker(minimumDate, maximumDate, datePickers);
    }

    private void setPreviewRounded() {
        ImageUtils.setRoundedClip(profilePreview, 20, 20);
        ImageUtils.setRoundedClip(governmentIdPreview, 10, 10);
    }

    private void showImageView(Image image) {
        modalUtils.showImageView(image, currentStage);
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