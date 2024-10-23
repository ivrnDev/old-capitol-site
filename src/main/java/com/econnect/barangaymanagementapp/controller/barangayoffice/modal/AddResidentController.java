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
import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.SuffixName.NONE;
import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.SuffixName.values;

public class AddResidentController {
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

    private final ModalUtils modalUtils;
    private final ImageService imageService;
    private final FileChooserUtils fileChooserUtils;
    private final FormValidator formValidator;
    private Stage currentStage;
    private File profileFile, governmentIdFile;
    List<TextField> textFields = Arrays.asList(
            residentIdInput, lastNameInput, firstNameInput, middleNameInput,
            birthplaceInput, occupationInput, emailInput, addressInput,
            contactNumberInput, fatherFirstNameInput, fatherLastNameInput,
            fatherMiddleNameInput, fatherOccupationInput, motherFirstNameInput,
            motherLastNameInput, motherMiddleNameInput, motherOccupationInput,
            citizenshipInput
    );
    List<DatePicker> datePickers = Arrays.asList(
            birthdatePicker, fatherBirthdatePicker, motherBirthdatePicker
    );
    List<ComboBox<String>> comboBoxes = Arrays.asList(
            suffixInput, sexComboBox, civilStatusComboBox, motherToungeComboBox,
            religionComboBox, bloodTypeComboBox, fatherSuffixNameComboBox,
            motherSuffixComboBox
    );

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

    private void setupInputFields() {
        LocalDate birthMinDate = LocalDate.now().minusYears(120);
        LocalDate birthMaxDate = LocalDate.now().minusYears(12);

        populateComboBoxes();
        DatePicker[] datePickers = {birthdatePicker, fatherBirthdatePicker, motherBirthdatePicker};
        formValidator.setupDatePicker(birthMinDate, birthMaxDate, datePickers);
        formValidator.addListeners(residentIdInput, formValidator.IS_NOT_EMPTY, "Resident ID cannot be empty.");
        formValidator.addListeners(lastNameInput, formValidator.IS_NOT_EMPTY, "Last name cannot be empty.");
        formValidator.addListeners(firstNameInput, formValidator.IS_NOT_EMPTY, "First name cannot be empty.");
        formValidator.addListeners(middleNameInput, formValidator.IS_NOT_EMPTY, "Middle name cannot be empty.");
        formValidator.addListeners(birthplaceInput, formValidator.IS_NOT_EMPTY, "Birthplace cannot be empty.");
        formValidator.addListeners(occupationInput, formValidator.IS_NOT_EMPTY, "Occupation cannot be empty.");
        formValidator.addListeners(emailInput, formValidator.IS_EMAIL, "Please enter a valid email address.");
        formValidator.addListeners(addressInput, formValidator.IS_NOT_EMPTY, "Address cannot be empty.");
        formValidator.addListeners(contactNumberInput, formValidator.IS_VALID_PHONE, "Please enter a valid phone number.");
        formValidator.addListeners(fatherFirstNameInput, formValidator.IS_NOT_EMPTY, "Father's first name cannot be empty.");
        formValidator.addListeners(fatherLastNameInput, formValidator.IS_NOT_EMPTY, "Father's last name cannot be empty.");
        formValidator.addListeners(fatherMiddleNameInput, formValidator.IS_NOT_EMPTY, "Father's middle name cannot be empty.");
        formValidator.addListeners(fatherOccupationInput, formValidator.IS_NOT_EMPTY, "Father's occupation cannot be empty.");
        formValidator.addListeners(motherFirstNameInput, formValidator.IS_NOT_EMPTY, "Mother's first name cannot be empty.");
        formValidator.addListeners(motherLastNameInput, formValidator.IS_NOT_EMPTY, "Mother's last name cannot be empty.");
        formValidator.addListeners(motherMiddleNameInput, formValidator.IS_NOT_EMPTY, "Mother's middle name cannot be empty.");
        formValidator.addListeners(motherOccupationInput, formValidator.IS_NOT_EMPTY, "Mother's occupation cannot be empty.");
        formValidator.addListeners(citizenshipInput, formValidator.IS_NOT_EMPTY, "Citizenship cannot be empty.");
        citizenshipInput.setEditable(false);
        citizenshipInput.setText("Filipino");
        suffixInput.setValue(NONE.getName());
        fatherSuffixNameComboBox.setValue(NONE.getName());
        motherSuffixComboBox.setValue(NONE.getName());
    }

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
//        residentIdInput.setText("RES-0001");
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
        boolean hasError = false;
        String errorMessage = "";

        TextField firstFieldError = null;
        DatePicker firstDatePickerField = null;
        boolean hasEmptyComboBox = false;
        boolean hasEmptyField = false;

        for (TextField field : textFields) {
            if (!formValidator.IS_NOT_EMPTY.test(field.getText())) {
                field.setStyle("-fx-border-color: red;");
                hasEmptyField = true;

                if (firstFieldError == null) {
                    firstFieldError = field;
                    field.requestFocus();
                }
            } else {
                field.setStyle("");
            }

            if (firstFieldError == null) {
                if (field.equals(emailInput) && !formValidator.IS_EMAIL.test(field.getText())) {
                    field.requestFocus();
                    errorMessage = "Please enter a valid email address.";
                    hasError = true;
                    break;
                }
            }

            if (firstFieldError == null) {
                if (field.equals(contactNumberInput) && !formValidator.IS_VALID_PHONE.test(field.getText())) {
                    field.requestFocus();
                    errorMessage = "Please enter a valid phone number.";
                    hasError = true;
                    break;
                }
            }
        }

        for (ComboBox<String> comboBox : comboBoxes) {
            if (comboBox.getValue() == null) {
                comboBox.setStyle("-fx-border-color: red;");
                hasEmptyComboBox = true;
            } else {
                comboBox.setStyle("");
            }
        }

        if (hasEmptyComboBox || hasEmptyField) {
            errorMessage = "Please fill out all required fields.";
            hasError = true;
        }

        if (!hasError) {
            for (DatePicker datePicker : datePickers) {
                if (!formValidator.DATE_VALIDATOR.test(datePicker.getEditor().getText())) {
                    if (firstDatePickerField == null) {
                        firstDatePickerField = datePicker;
                        datePicker.requestFocus();
                    }
                    datePicker.setStyle("-fx-border-color: red;");
                    hasError = true;
                    errorMessage = "Please enter a valid date in the format MM/DD/YYYY.";
                    break;
                }
            }
        }

        if (!hasError) {
            if (formValidator.hasEmptyFile(governmentIdFile, uploadGovernmentId)) {
                errorMessage = "Please upload a government ID.";
                hasError = true;
            }

            if (formValidator.hasEmptyFile(profileFile, uploadProfile)) {
                errorMessage = "Please upload a profile picture.";
                hasError = true;
            }
        }

        if (hasError) {
            modalUtils.showModal(Modal.ERROR, "Error", errorMessage);
        }
    }

    private void populateComboBoxes() {
        List<String> suffixNamesChoices = Arrays.stream(values()).map(suffix -> suffix.getName()).toList();
        suffixInput.getItems().addAll(suffixNamesChoices);
        fatherSuffixNameComboBox.getItems().addAll(suffixNamesChoices);
        motherSuffixComboBox.getItems().addAll(suffixNamesChoices);
        civilStatusComboBox.getItems().addAll(Arrays.stream(ResidentInfomationType.CivilStatus.values()).map(civilStatus -> civilStatus.getName()).toList());
        religionComboBox.getItems().addAll(Arrays.stream(ResidentInfomationType.Religion.values()).map(religion -> religion.getName()).toList());
        bloodTypeComboBox.getItems().addAll(Arrays.stream(ResidentInfomationType.BloodType.values()).map(bloodType -> bloodType.getName()).toList());
        sexComboBox.getItems().addAll(Arrays.stream(GenderType.values()).map(gender -> gender.getName()).toList());
        motherToungeComboBox.getItems().addAll(Arrays.stream(ResidentInfomationType.MotherTongue.values()).map(motherTongue -> motherTongue.getName()).toList());
    }

    private void setPreviewRounded() {
        ImageUtils.setRoundedClip(profilePreview, 20, 20);
        ImageUtils.setRoundedClip(governmentIdPreview, 10, 10);
    }

    private void showImageView(Image image) {
        modalUtils.showImageView(image, currentStage);
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