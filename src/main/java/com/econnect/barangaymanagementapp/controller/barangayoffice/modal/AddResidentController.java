package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.FileType;
import com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.CivilStatus;
import com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.MotherTongue;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FormValidator;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.FileChooserUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.econnect.barangaymanagementapp.enumeration.type.FileType.GOVERNMENT_ID;
import static com.econnect.barangaymanagementapp.enumeration.type.FileType.PROFILE_PICTURE;
import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.*;
import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.MotherTongue.*;
import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.SuffixName.NONE;
import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.SuffixName.values;

public class AddResidentController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField lastNameInput, firstNameInput, middleNameInput, birthplaceInput, occupationInput, emailInput, addressInput, mobileNumberInput, telephoneInput, fatherFirstNameInput, fatherLastNameInput, fatherMiddleNameInput, fatherOccupationInput, motherFirstNameInput, motherLastNameInput, motherMiddleNameInput, motherOccupationInput,
            citizenshipInput, spouseFirstNameInput, spouseLastNameInput, spouseMiddleNameInput, spouseOccupationInput, tinIdNumberInput, emergencyFirstNameInput, emergencyLastNameInput, emergencyMiddleNameInput, emergencyMobileNumberInput, emergencyRelationshipInput;
    @FXML
    private ComboBox<String> suffixComboBox, sexComboBox, civilStatusComboBox, motherToungeComboBox, religionComboBox, bloodTypeComboBox, fatherSuffixComboBox, motherSuffixComboBox, spouseSuffixComboBox, residencyStatusComboBox;
    @FXML
    private DatePicker birthdatePicker, fatherBirthdatePicker, motherBirthdatePicker, spouseBirthdatePicker, validIdExpirationDatePicker;
    @FXML
    private HBox uploadProfile, viewProfileBtn, uploadGovernmentId, viewGovernmentIdBtn, uploadTinId, viewTinId;
    @FXML
    private VBox spouseInputContainer;
    @FXML
    private Label profileLabel, governmentIdLabel;
    @FXML
    private ImageView profilePreview, governmentIdPreview, tinIdPreview, closeBtn;
    @FXML
    private Button cancelBtn, confirmBtn;
    @FXML
    private CheckBox ownEarningsCheckBox, ownPensionCheckBox, stocksCheckBox, dependentCheckBox, spouseSalaryCheckBox, spousePensionCheckBox, insuranceCheckBox, rentalCheckBox, savingsCheckBox;
    @FXML
    private ToggleGroup educationalAttainment;

    private final ModalUtils modalUtils;
    private final ImageService imageService;
    private final FileChooserUtils fileChooserUtils;
    private final FormValidator formValidator;
    private final ResidentService residentService;
    private Stage currentStage;
    private File profileFile, governmentIdFile, tidIdFile;

    public AddResidentController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.imageService = dependencyInjector.getImageService();
        this.fileChooserUtils = dependencyInjector.getFileChooserUtils();
        this.formValidator = dependencyInjector.getFormValidator();
        this.residentService = dependencyInjector.getResidentService();
        Platform.runLater(() -> this.currentStage = (Stage) confirmBtn.getScene().getWindow());
    }

    public void initialize() {
        setupPreviewRounded();
        setupActionButtons();
        setupInputFields();
        mockData();
    }

    private void addResident() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Resident employee = createResidentFromInput();

        Task<Void> addResidentTask = new Task<>() {
            @Override
            protected Void call() {
                return processResidentCreation(employee);
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
                Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while adding the resident."));
            }
        };

        new Thread(addResidentTask).start();
    }

    private Void processResidentCreation(Resident resident) {
        String id = residentService.generateResidentId();
        String profileUrl = imageService.uploadImage(Firestore.PROFILE_PICTURE, profileFile, id);
        String governmentIDUrl = imageService.uploadImage(Firestore.VALID_ID, governmentIdFile, id);
        String tinIdURl = imageService.uploadImage(Firestore.TIN_ID, tidIdFile, id);
        resident.setId(id);
        resident.setProfileUrl(profileUrl);
        resident.setValidIdUrl(governmentIDUrl);
        resident.setTinIdUrl(tinIdURl);
        try (Response response = residentService.createResident(resident)) {
            if (response.isSuccessful()) {
                Platform.runLater(() -> modalUtils.showModal(Modal.SUCCESS, "Success", "Resident added successfully"));
            } else {
                Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Failed", "Failed to add resident"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Resident createResidentFromInput() {
        return Resident.builder()
                .firstName(firstNameInput.getText())
                .middleName(middleNameInput.getText())
                .lastName(lastNameInput.getText())
                .nameExtension(suffixComboBox.getValue())
                .mobileNumber("0" + mobileNumberInput.getText())
                .telephoneNumber(telephoneInput.getText())
                .email(emailInput.getText())
                .address(addressInput.getText())
                .birthdate(motherBirthdatePicker.getValue() != null ? DateFormatter.toStandardFormat(motherBirthdatePicker.getValue()) : null)
                .birthplace(birthplaceInput.getText())
                .citizenship(citizenshipInput.getText())
                .civilStatus(CivilStatus.fromName(civilStatusComboBox.getValue()))
                .motherTounge(fromName(motherToungeComboBox.getValue()))
                .bloodType(BloodType.fromName(bloodTypeComboBox.getValue()))
                .religion(Religion.fromName(religionComboBox.getValue()))
                .occupation(occupationInput.getText())
                .age(birthdatePicker.getValue() != null ? DateFormatter.calculateAge(birthdatePicker.getValue()) : null)
                .sex(GenderType.fromName(sexComboBox.getValue()))

                .fatherFirstName(fatherFirstNameInput.getText())
                .fatherMiddleName(fatherMiddleNameInput.getText())
                .fatherLastName(fatherLastNameInput.getText())
                .fatherSuffixName(fatherSuffixComboBox.getValue())
                .fatherOccupation(fatherOccupationInput.getText())
                .fatherBirthdate(fatherBirthdatePicker.getValue() != null ? DateFormatter.toStandardFormat(fatherBirthdatePicker.getValue()) : null)

                .motherFirstName(motherFirstNameInput.getText())
                .motherMiddleName(motherMiddleNameInput.getText())
                .motherLastName(motherLastNameInput.getText())
                .motherSuffixName(motherSuffixComboBox.getValue())
                .motherOccupation(motherOccupationInput.getText())
                .motherBirthdate(motherBirthdatePicker.getValue() != null ? DateFormatter.toStandardFormat(motherBirthdatePicker.getValue()) : null)

                .spouseFirstName(spouseFirstNameInput.getText())
                .spouseMiddleName(spouseMiddleNameInput.getText())
                .spouseLastName(spouseLastNameInput.getText())
                .spouseSuffixName(spouseSuffixComboBox.getValue())
                .spouseOccupation(spouseOccupationInput.getText())
                .spouseBirthdate(spouseBirthdatePicker.getValue() != null ? DateFormatter.toStandardFormat(spouseBirthdatePicker.getValue()) : null)

                .emergencyFirstName(emergencyFirstNameInput.getText())
                .emergencyMiddleName(emergencyMiddleNameInput.getText())
                .emergencyLastName(emergencyLastNameInput.getText())
                .emergencyMobileNumber("0" + emergencyMobileNumberInput.getText())
                .emergencyRelationship(emergencyRelationshipInput.getText())

                .status(StatusType.ResidentStatus.VERIFIED)
                .residencyStatus(ResidencyStatus.fromName(residencyStatusComboBox.getValue()))
                .sourceOfIncome(getSourceOfIncome())
                .educationalAttainment(educationalAttainment.getSelectedToggle() != null
                        ? ((RadioButton) educationalAttainment.getSelectedToggle()).getText()
                        : null)
                .profileUrl(profileFile != null ? profileFile.toURI().toString() : null)
                .validIdUrl(governmentIdFile != null ? governmentIdFile.toURI().toString() : null)
                .tinIdUrl(tidIdFile != null ? tidIdFile.toURI().toString() : null)
                .tinIdNumber(tinIdNumberInput.getText())
                .validIdExpiration(validIdExpirationDatePicker.getValue() != null ? DateFormatter.toStandardFormat(validIdExpirationDatePicker.getValue()) : null)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }

    private void setupInputFields() {
        civilStatusComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (CivilStatus.MARRIED.getName().equals(newValue)) {
                spouseInputContainer.setManaged(true);
                spouseInputContainer.setVisible(true);
            } else {
                spouseInputContainer.setManaged(false);
                spouseInputContainer.setVisible(false);
            }
        });
        LocalDate birthMinDate = LocalDate.now().minusYears(120);
        LocalDate birthMaxDate = LocalDate.now().minusYears(12);

        populateComboBoxes();
        DatePicker[] datePickers = {birthdatePicker, fatherBirthdatePicker, motherBirthdatePicker, spouseBirthdatePicker};
        formValidator.setupDatePicker(birthMinDate, birthMaxDate, datePickers);
        formValidator.setupDatePicker(LocalDate.now(), LocalDate.now().plusYears(10), validIdExpirationDatePicker);
        formValidator.addListeners(lastNameInput, formValidator.IS_NOT_EMPTY, "Last name cannot be empty.");
        formValidator.addListeners(firstNameInput, formValidator.IS_NOT_EMPTY, "First name cannot be empty.");
        formValidator.addListeners(middleNameInput, formValidator.IS_NOT_EMPTY, "Middle name cannot be empty.");
        formValidator.addListeners(birthplaceInput, formValidator.IS_NOT_EMPTY, "Birthplace cannot be empty.");
        formValidator.addListeners(occupationInput, formValidator.IS_NOT_EMPTY, "Occupation cannot be empty.");
        formValidator.addListeners(emailInput, formValidator.IS_EMAIL, "Please enter a valid email address.");
        formValidator.addListeners(addressInput, formValidator.IS_NOT_EMPTY, "Address cannot be empty.");
        formValidator.addListeners(mobileNumberInput, formValidator.IS_VALID_PHONE, "Please enter a valid phone number.");
        formValidator.addListeners(telephoneInput, formValidator.IS_VALID_TELEPHONE, "Please enter a valid phone number.");
        formValidator.addListeners(fatherFirstNameInput, formValidator.IS_NOT_EMPTY, "Father's first name cannot be empty.");
        formValidator.addListeners(fatherLastNameInput, formValidator.IS_NOT_EMPTY, "Father's last name cannot be empty.");
        formValidator.addListeners(fatherMiddleNameInput, formValidator.IS_NOT_EMPTY, "Father's middle name cannot be empty.");
        formValidator.addListeners(fatherOccupationInput, formValidator.IS_NOT_EMPTY, "Father's occupation cannot be empty.");
        formValidator.addListeners(motherFirstNameInput, formValidator.IS_NOT_EMPTY, "Mother's first name cannot be empty.");
        formValidator.addListeners(motherLastNameInput, formValidator.IS_NOT_EMPTY, "Mother's last name cannot be empty.");
        formValidator.addListeners(motherMiddleNameInput, formValidator.IS_NOT_EMPTY, "Mother's middle name cannot be empty.");
        formValidator.addListeners(motherOccupationInput, formValidator.IS_NOT_EMPTY, "Mother's occupation cannot be empty.");
        formValidator.addListeners(bloodTypeComboBox, formValidator.IS_NOT_EMPTY, "Blood type cannot be empty.");
        formValidator.addListeners(religionComboBox, formValidator.IS_NOT_EMPTY, "Religion cannot be empty.");
        formValidator.addListeners(sexComboBox, formValidator.IS_NOT_EMPTY, "Sex cannot be empty");
        formValidator.addListeners(civilStatusComboBox, formValidator.IS_NOT_EMPTY, "Civil status cannot be empty.");
        formValidator.addListeners(motherToungeComboBox, formValidator.IS_NOT_EMPTY, "Mother tounge cannot be empty.");
        formValidator.addListeners(suffixComboBox, formValidator.IS_NOT_EMPTY, "Suffix cannot be empty.");
        formValidator.addListeners(fatherSuffixComboBox, formValidator.IS_NOT_EMPTY, "Father's suffix cannot be empty.");
        formValidator.addListeners(motherSuffixComboBox, formValidator.IS_NOT_EMPTY, "Mother's suffix cannot be empty.");
        formValidator.addListeners(residencyStatusComboBox, formValidator.IS_NOT_EMPTY, "Residency status cannot be empty.");
        formValidator.addListeners(tinIdNumberInput, formValidator.IS_NUMBER, "TIN ID must be a number.");
        formValidator.addListeners(emergencyFirstNameInput, formValidator.IS_NOT_EMPTY, "Emergency contact's first name cannot be empty.");
        formValidator.addListeners(emergencyLastNameInput, formValidator.IS_NOT_EMPTY, "Emergency contact's last name cannot be empty.");
        formValidator.addListeners(emergencyMiddleNameInput, formValidator.IS_NOT_EMPTY, "Emergency contact's middle name cannot be empty.");
        formValidator.addListeners(emergencyMobileNumberInput, formValidator.IS_VALID_PHONE, "Please enter a valid phone number.");
        formValidator.addListeners(emergencyRelationshipInput, formValidator.IS_NOT_EMPTY, "Emergency contact's relationship cannot be empty.");

        if (spouseInputContainer.isVisible()) {
            formValidator.addListeners(spouseFirstNameInput, formValidator.IS_NOT_EMPTY, "Spouse's first name cannot be empty.");
            formValidator.addListeners(spouseLastNameInput, formValidator.IS_NOT_EMPTY, "Spouse's last name cannot be empty.");
            formValidator.addListeners(spouseMiddleNameInput, formValidator.IS_NOT_EMPTY, "Spouse's middle name cannot be empty.");
            formValidator.addListeners(spouseOccupationInput, formValidator.IS_NOT_EMPTY, "Spouse's occupation cannot be empty.");
            formValidator.addListeners(spouseSuffixComboBox, formValidator.IS_NOT_EMPTY, "Spouse's suffix cannot be empty.");
        }

        citizenshipInput.setEditable(false);
        citizenshipInput.setText("Filipino");
        suffixComboBox.setValue(NONE.getName());
        fatherSuffixComboBox.setValue(NONE.getName());
        motherSuffixComboBox.setValue(NONE.getName());
        if (spouseInputContainer.isVisible()) {
            spouseSuffixComboBox.setValue(NONE.getName());
        }
        motherToungeComboBox.setValue(TAGALOG.getName());
        civilStatusComboBox.setValue(CivilStatus.SINGLE.getName());
        religionComboBox.setValue(Religion.CATHOLICISM.getName());
        residencyStatusComboBox.setValue(ResidencyStatus.OWNER.getName());
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
                } else if (fileType.equals(FileType.TIN_ID)) {
                    tidIdFile = file;
                    formValidator.validateFile(tidIdFile, uploadTinId);
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
        lastNameInput.setText("Dela Cruz");
        firstNameInput.setText("Juan");
        middleNameInput.setText("Santos");
        suffixComboBox.setValue("Jr.");
        birthplaceInput.setText("dsa");
        occupationInput.setText("Software Engineer");
        emailInput.setText("villamora@gmail.com");
        addressInput.setText("1234 Barangay St., Barangay, City");
        mobileNumberInput.setText("09123456789");
        fatherFirstNameInput.setText("Juan");
        fatherLastNameInput.setText("Dela Cruz");
        fatherMiddleNameInput.setText("Santos");
        fatherSuffixComboBox.setValue("Jr.");
        fatherOccupationInput.setText("Software Engineer");
        motherFirstNameInput.setText("Maria");
        motherLastNameInput.setText("Dela Cruz");
        motherMiddleNameInput.setText("Santos");
        motherSuffixComboBox.setValue("Jr.");
        motherOccupationInput.setText("Software Engineer");
        citizenshipInput.setText("Filipino");
        suffixComboBox.setValue("Jr.");
        suffixComboBox.setValue("Jr.");
        sexComboBox.setValue("Male");
        civilStatusComboBox.setValue("Single");
        motherToungeComboBox.setValue("Tagalog");
        religionComboBox.setValue("Catholic");
        bloodTypeComboBox.setValue("O+");
        fatherSuffixComboBox.setValue("Sr.");
        motherSuffixComboBox.setValue("Jr.");
    }

    private void setupActionButtons() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateForm());

        viewProfileBtn.setOnMouseClicked(_ -> showImageView(profilePreview.getImage()));
        viewGovernmentIdBtn.setOnMouseClicked(_ -> showImageView(governmentIdPreview.getImage()));
        viewTinId.setOnMouseClicked(_ -> showImageView(tinIdPreview.getImage()));

        uploadProfile.setOnMouseClicked(_ -> uploadImage(viewProfileBtn, profilePreview, profileLabel, PROFILE_PICTURE));
        uploadGovernmentId.setOnMouseClicked(_ -> uploadImage(viewGovernmentIdBtn, governmentIdPreview, governmentIdLabel, GOVERNMENT_ID));
        uploadTinId.setOnMouseClicked(_ -> uploadImage(viewTinId, tinIdPreview, governmentIdLabel, FileType.TIN_ID));
    }

    private void validateForm() {
        List<TextField> textFields = new ArrayList<>(Arrays.asList(
                lastNameInput, firstNameInput, middleNameInput,
                birthplaceInput, occupationInput, emailInput, addressInput,
                telephoneInput, mobileNumberInput, fatherFirstNameInput, fatherLastNameInput,
                fatherMiddleNameInput, fatherOccupationInput, motherFirstNameInput,
                motherLastNameInput, motherMiddleNameInput, motherOccupationInput,
                citizenshipInput, tinIdNumberInput, emergencyFirstNameInput, emergencyLastNameInput,
                emergencyMiddleNameInput, emergencyMobileNumberInput, emergencyRelationshipInput
        ));

        List<DatePicker> datePickers = new ArrayList<>(Arrays.asList(
                birthdatePicker, fatherBirthdatePicker, motherBirthdatePicker, validIdExpirationDatePicker
        ));

        List<ComboBox<String>> comboBoxes = new ArrayList<>(Arrays.asList(
                suffixComboBox, sexComboBox, civilStatusComboBox, motherToungeComboBox,
                religionComboBox, bloodTypeComboBox, fatherSuffixComboBox,
                motherSuffixComboBox, residencyStatusComboBox
        ));

        if (spouseInputContainer.isVisible()) {
            textFields.addAll(Arrays.asList(
                    spouseFirstNameInput, spouseLastNameInput, spouseMiddleNameInput, spouseOccupationInput
            ));
            comboBoxes.add(spouseSuffixComboBox);
            datePickers.add(spouseBirthdatePicker);
        }
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
                if (field.equals(mobileNumberInput) && !formValidator.IS_VALID_PHONE.test(field.getText())) {
                    field.requestFocus();
                    errorMessage = "Please enter a valid mobile number.";
                    hasError = true;
                    break;
                }
            }

            if (firstFieldError == null) {
                if (field.equals(telephoneInput) && !formValidator.IS_VALID_TELEPHONE.test(field.getText())) {
                    field.requestFocus();
                    errorMessage = "Please enter a valid telephone number.";
                    hasError = true;
                    break;
                }
            }

            if (firstFieldError == null) {
                if (field.equals(tinIdNumberInput) && !formValidator.IS_NUMBER.test(field.getText())) {
                    field.requestFocus();
                    errorMessage = "TIN ID must be a number.";
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

        if (!ownEarningsCheckBox.isSelected() && !ownPensionCheckBox.isSelected() && !stocksCheckBox.isSelected() && !dependentCheckBox.isSelected() && !spouseSalaryCheckBox.isSelected() && !spousePensionCheckBox.isSelected() && !insuranceCheckBox.isSelected() && !rentalCheckBox.isSelected() && !savingsCheckBox.isSelected()) {
            errorMessage = "Please select at least one source of income.";
            hasError = true;
        }

        if (!hasError) {
            if (formValidator.hasEmptyFile(tidIdFile, uploadTinId)) {
                errorMessage = "Please upload a TIN ID.";
                hasError = true;
            }

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
            return;
        }
        addResident();
    }

    private void populateComboBoxes() {
        List<String> suffixNamesChoices = Arrays.stream(values()).map(suffix -> suffix.getName()).toList();
        suffixComboBox.getItems().addAll(suffixNamesChoices);
        fatherSuffixComboBox.getItems().addAll(suffixNamesChoices);
        motherSuffixComboBox.getItems().addAll(suffixNamesChoices);
        spouseSuffixComboBox.getItems().addAll(suffixNamesChoices);
        civilStatusComboBox.getItems().addAll(Arrays.stream(CivilStatus.values()).map(civilStatus -> civilStatus.getName()).toList());
        religionComboBox.getItems().addAll(Arrays.stream(Religion.values()).map(religion -> religion.getName()).toList());
        bloodTypeComboBox.getItems().addAll(Arrays.stream(BloodType.values()).map(bloodType -> bloodType.getName()).toList());
        sexComboBox.getItems().addAll(Arrays.stream(GenderType.values()).map(gender -> gender.getName()).toList());
        motherToungeComboBox.getItems().addAll(Arrays.stream(MotherTongue.values()).map(motherTongue -> motherTongue.getName()).toList());
        residencyStatusComboBox.getItems().addAll(Arrays.stream(ResidencyStatus.values()).map(residencyStatus -> residencyStatus.getName()).toList());
    }

    private String getSourceOfIncome() {
        List<CheckBox> incomeSources = Arrays.asList(
                ownEarningsCheckBox, ownPensionCheckBox,
                stocksCheckBox, dependentCheckBox, spouseSalaryCheckBox,
                spousePensionCheckBox, insuranceCheckBox, rentalCheckBox
        );

        return incomeSources.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.joining(", "));
    }

    private void setupPreviewRounded() {
        ImageUtils.setCircleClip(profilePreview);
        ImageUtils.setCircleClip(governmentIdPreview);
        ImageUtils.setCircleClip(tinIdPreview);
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