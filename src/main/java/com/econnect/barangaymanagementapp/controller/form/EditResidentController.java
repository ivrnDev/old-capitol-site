package com.econnect.barangaymanagementapp.controller.form;

import com.econnect.barangaymanagementapp.controller.base.BaseViewController;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.UploadImageUtils;
import com.econnect.barangaymanagementapp.util.Validator;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
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
import javafx.stage.Stage;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.*;
import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.SuffixName.values;

public class EditResidentController implements BaseViewController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField lastNameInput, firstNameInput, middleNameInput, birthplaceInput, occupationInput, emailInput, addressInput, mobileNumberInput, telephoneInput, fatherFirstNameInput, fatherLastNameInput, fatherMiddleNameInput, fatherOccupationInput, motherFirstNameInput, motherLastNameInput, motherMiddleNameInput, motherOccupationInput,
            citizenshipInput, spouseFirstNameInput, spouseLastNameInput, spouseMiddleNameInput, spouseOccupationInput, tinIdNumberInput, emergencyFirstNameInput, emergencyLastNameInput, emergencyMiddleNameInput, emergencyMobileNumberInput, emergencyRelationshipInput;
    @FXML
    private ComboBox<String> suffixComboBox, sexComboBox, civilStatusComboBox, motherToungeComboBox, religionComboBox, bloodTypeComboBox, fatherSuffixComboBox, motherSuffixComboBox, spouseSuffixComboBox, residencyStatusComboBox;
    @FXML
    private DatePicker birthdatePicker, validIdExpirationDatePicker;
    @FXML
    private HBox uploadProfile, viewProfileBtn, uploadGovernmentId, viewGovernmentIdBtn, uploadTinId, viewTinId, checkBoxContainer;
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
    private final Validator validator;
    private final UploadImageUtils uploadImageUtils;
    private final ResidentService residentService;
    private Stage currentStage;
    private Image profileImage, governmentIdImage, tinImage;
    private String residentId;

    public EditResidentController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.imageService = dependencyInjector.getImageService();
        this.validator = dependencyInjector.getValidator();
        this.uploadImageUtils = dependencyInjector.getUploadImageUtils();
        this.residentService = dependencyInjector.getResidentService();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupPreviewRounded();
        setupActionButtons();
        setupInputFields();
        getResident();
    }

    private void editResident() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Resident employee = getResidentByInput();

        Task<Void> editResident = new Task<>() {
            @Override
            protected Void call() {
                return processResidentUpdate(employee);
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                closeWindow();
                modalUtils.showModal(Modal.SUCCESS, "Success", "Resident successfully edited.");
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                modalUtils.showModal(Modal.ERROR, "Failed", "Failed to add resident. Please try again.");

            }
        };

        new Thread(editResident).start();
    }

    private void getResident() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Task<Optional<Resident>> getResidentTask = new Task<>() {
            @Override
            protected Optional<Resident> call() {
                return residentService.findResidentById(residentId);
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false);
                Resident resident = getValue().get();
                populateFields(resident);
                rootPane.getChildren().remove(loadingIndicator);
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                modalUtils.showModal(Modal.ERROR, "Failed", "Failed to add resident. Please try again.");
            }
        };

        new Thread(getResidentTask).start();
    }

    private Void processResidentUpdate(Resident resident) {
        String id = residentService.generateResidentId();
        String profileUrl = imageService.uploadImage(Firestore.PROFILE_PICTURE, profileImage, id);
        String governmentIDUrl = imageService.uploadImage(Firestore.VALID_ID, governmentIdImage, id);
        String tinIdURl = tinImage != null ? imageService.uploadImage(Firestore.TIN_ID, tinImage, id) : null;
        resident.setId(id);
        resident.setProfileUrl(profileUrl);
        resident.setValidIdUrl(governmentIDUrl);
        resident.setTinIdUrl(tinIdURl);
        residentService.updateResident(resident);
        return null;
    }

    private Resident getResidentByInput() {
        return Resident.builder()
                .firstName(firstNameInput.getText())
                .middleName(middleNameInput.getText())
                .lastName(lastNameInput.getText())
                .nameExtension(suffixComboBox.getValue())
                .mobileNumber(mobileNumberInput.getText())
                .telephoneNumber(!telephoneInput.getText().isEmpty() ? telephoneInput.getText() : null)
                .email(emailInput.getText())
                .address(addressInput.getText())
                .birthdate(birthdatePicker.getValue() != null ? DateFormatter.formatToUsShortDate(birthdatePicker.getValue()) : null)
                .birthplace(birthplaceInput.getText())
                .citizenship(citizenshipInput.getText())
                .civilStatus(CivilStatus.fromName(civilStatusComboBox.getValue()))
                .motherTounge(motherToungeComboBox.getValue())
                .bloodType(BloodType.fromName(bloodTypeComboBox.getValue()))
                .religion(religionComboBox.getValue())
                .occupation(occupationInput.getText())
                .age(birthdatePicker.getValue() != null ? DateFormatter.calculateAgeFromBirthdate(birthdatePicker.getValue()) : null)
                .sex(GenderType.fromName(sexComboBox.getValue()))
                .fatherFirstName(fatherFirstNameInput.getText())
                .fatherMiddleName(fatherMiddleNameInput.getText())
                .fatherLastName(fatherLastNameInput.getText())
                .fatherSuffixName(fatherSuffixComboBox.getValue())
                .fatherOccupation(fatherOccupationInput.getText())

                .motherFirstName(motherFirstNameInput.getText())
                .motherMiddleName(motherMiddleNameInput.getText())
                .motherLastName(motherLastNameInput.getText())
                .motherSuffixName(motherSuffixComboBox.getValue())
                .motherOccupation(motherOccupationInput.getText())

                .spouseFirstName(spouseFirstNameInput.getText())
                .spouseMiddleName(spouseMiddleNameInput.getText())
                .spouseLastName(spouseLastNameInput.getText())
                .spouseSuffixName(spouseSuffixComboBox.getValue())
                .spouseOccupation(spouseOccupationInput.getText())

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
                .tinIdNumber(!tinIdNumberInput.getText().isEmpty() ? tinIdNumberInput.getText() : null)
                .validIdExpiration(validIdExpirationDatePicker.getValue() != null ? DateFormatter.formatLocalDateToUsShortDate(validIdExpirationDatePicker.getValue()) : null)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }

    private void setupInputFields() {
        LocalDate birthMinDate = LocalDate.now().minusYears(120);
        LocalDate birthMaxDate = LocalDate.now().minusYears(12);
        DatePicker[] datePickers = {birthdatePicker};
        TextField[] letterOnlyFields = {
                lastNameInput, firstNameInput, middleNameInput,
                birthplaceInput, occupationInput,
                fatherFirstNameInput, fatherLastNameInput,
                fatherMiddleNameInput, fatherOccupationInput, motherFirstNameInput,
                motherLastNameInput, motherMiddleNameInput, motherOccupationInput,
                emergencyFirstNameInput, emergencyLastNameInput,
                emergencyMiddleNameInput, emergencyRelationshipInput, spouseFirstNameInput, spouseLastNameInput, spouseMiddleNameInput, spouseOccupationInput};
        TextField[] maxOnlyInput = {addressInput};
        List<ComboBox<String>> comboBoxList = List.of(
                suffixComboBox, sexComboBox, civilStatusComboBox, motherToungeComboBox,
                religionComboBox, bloodTypeComboBox, fatherSuffixComboBox,
                motherSuffixComboBox, residencyStatusComboBox
        );


        validator.setLettersOnlyListener(30, letterOnlyFields);
        validator.setupMaxLengthListener(100, maxOnlyInput);
        validator.setupComboBox(comboBoxList);
        validator.setupDatePicker(birthMinDate, birthMaxDate, datePickers);
        validator.setupDatePicker(LocalDate.now(), LocalDate.now().plusYears(10), validIdExpirationDatePicker);

        //Special characters not allowed validator
        validator.createMobileNumberFormatter(11, mobileNumberInput, emergencyMobileNumberInput);
        validator.createTelephoneFormatter(telephoneInput);
        validator.createEmailFormatter(emailInput);
        validator.createTinIdFormatter(tinIdNumberInput);

        populateComboBoxes();
        civilStatusComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (CivilStatus.MARRIED.getName().equals(newValue)) {
                spouseInputContainer.setManaged(true);
                spouseInputContainer.setVisible(true);
            } else {
                spouseInputContainer.setManaged(false);
                spouseInputContainer.setVisible(false);
            }
        });
        citizenshipInput.setEditable(false);
        citizenshipInput.setText("Filipino");

    }

    public void populateFields(Resident resident) {
        lastNameInput.setText(resident.getLastName());
        firstNameInput.setText(resident.getFirstName());
        middleNameInput.setText(resident.getMiddleName());
        suffixComboBox.setValue(resident.getNameExtension());
        birthplaceInput.setText(resident.getBirthplace());
        occupationInput.setText(resident.getOccupation());
        emailInput.setText(resident.getEmail());
        addressInput.setText(resident.getAddress());
        mobileNumberInput.setText(resident.getMobileNumber());
        telephoneInput.setText(resident.getTelephoneNumber());
        fatherFirstNameInput.setText(resident.getFatherFirstName());
        fatherLastNameInput.setText(resident.getFatherLastName());
        fatherMiddleNameInput.setText(resident.getFatherMiddleName());
        fatherSuffixComboBox.setValue(resident.getFatherSuffixName());
        fatherOccupationInput.setText(resident.getFatherOccupation());
        motherFirstNameInput.setText(resident.getMotherFirstName());
        motherLastNameInput.setText(resident.getMotherLastName());
        motherMiddleNameInput.setText(resident.getMotherMiddleName());
        motherSuffixComboBox.setValue(resident.getMotherSuffixName());
        motherOccupationInput.setText(resident.getMotherOccupation());
        spouseFirstNameInput.setText(resident.getSpouseFirstName());
        spouseLastNameInput.setText(resident.getSpouseLastName());
        spouseMiddleNameInput.setText(resident.getSpouseMiddleName());
        spouseSuffixComboBox.setValue(resident.getSpouseSuffixName());
        spouseOccupationInput.setText(resident.getSpouseOccupation());
        emergencyFirstNameInput.setText(resident.getEmergencyFirstName());
        emergencyLastNameInput.setText(resident.getEmergencyLastName());
        emergencyMiddleNameInput.setText(resident.getEmergencyMiddleName());
        emergencyMobileNumberInput.setText(resident.getEmergencyMobileNumber());
        emergencyRelationshipInput.setText(resident.getEmergencyRelationship());
        birthdatePicker.setValue(resident.getBirthdate() != null ? DateFormatter.convertToDatePickerLocalDate(resident.getBirthdate()) : null);
        validIdExpirationDatePicker.setValue(resident.getValidIdExpiration() != null ? DateFormatter.convertToDatePickerLocalDate(resident.getValidIdExpiration()) : null);
        sexComboBox.setValue(resident.getSex().getName());
        civilStatusComboBox.setValue(resident.getCivilStatus().getName());
        motherToungeComboBox.setValue(resident.getMotherTounge());
        religionComboBox.setValue(resident.getReligion());
        bloodTypeComboBox.setValue(resident.getBloodType().getName());
        residencyStatusComboBox.setValue(resident.getResidencyStatus().getName());
        tinIdNumberInput.setText(resident.getTinIdNumber());
        citizenshipInput.setText(resident.getCitizenship());
        educationalAttainment.selectToggle(educationalAttainment.getToggles().stream()
                .filter(toggle -> ((RadioButton) toggle).getText().equals(resident.getEducationalAttainment()))
                .findFirst().orElse(null));
//        profilePreview.setImage(new Image(resident.getProfileUrl()));
//        governmentIdPreview.setImage(new Image(resident.getValidIdUrl()));
//        tinIdPreview.setImage(new Image(resident.getTinIdUrl()));
        ownEarningsCheckBox.setSelected(resident.getSourceOfIncome().toLowerCase().contains("own earnings, salary, wages".toLowerCase().trim()));
        ownPensionCheckBox.setSelected(resident.getSourceOfIncome().toLowerCase().contains("own pension".toLowerCase().trim()));
        stocksCheckBox.setSelected(resident.getSourceOfIncome().toLowerCase().contains("stocks".toLowerCase().trim()));
        dependentCheckBox.setSelected(resident.getSourceOfIncome().toLowerCase().contains("dependent".toLowerCase().trim()));
        spouseSalaryCheckBox.setSelected(resident.getSourceOfIncome().toLowerCase().contains("spouse's salary".toLowerCase().trim()));
        spousePensionCheckBox.setSelected(resident.getSourceOfIncome().toLowerCase().contains("spouse's pension".toLowerCase().trim()));
        insuranceCheckBox.setSelected(resident.getSourceOfIncome().toLowerCase().contains("insurance".toLowerCase().trim()));
        rentalCheckBox.setSelected(resident.getSourceOfIncome().toLowerCase().contains("rental".toLowerCase().trim()));
        savingsCheckBox.setSelected(resident.getSourceOfIncome().toLowerCase().contains("savings".toLowerCase().trim()));
    }

    private void validateForm() {
        TextField[] textFields = {
                lastNameInput, firstNameInput, middleNameInput,
                birthplaceInput, occupationInput, emailInput, addressInput, mobileNumberInput, fatherFirstNameInput, fatherLastNameInput,
                fatherMiddleNameInput, fatherOccupationInput, motherFirstNameInput,
                motherLastNameInput, motherMiddleNameInput, motherOccupationInput,
                citizenshipInput, emergencyFirstNameInput, emergencyLastNameInput,
                emergencyMiddleNameInput, emergencyMobileNumberInput, emergencyRelationshipInput};
        TextField[] spouseTextField = {spouseFirstNameInput, spouseLastNameInput, spouseMiddleNameInput, spouseOccupationInput};
        DatePicker[] datePickers = {birthdatePicker, validIdExpirationDatePicker};
        ComboBox[] comboBoxes = {suffixComboBox, sexComboBox, civilStatusComboBox, motherToungeComboBox,
                religionComboBox, bloodTypeComboBox, fatherSuffixComboBox,
                motherSuffixComboBox, residencyStatusComboBox};
        CheckBox[] checkBoxes = {ownEarningsCheckBox, ownPensionCheckBox, stocksCheckBox, dependentCheckBox, spouseSalaryCheckBox, spousePensionCheckBox, insuranceCheckBox, rentalCheckBox, savingsCheckBox};
        ComboBox[] spouseComboBox = {spouseSuffixComboBox};
        Image[] images = {profileImage, governmentIdImage};
        HBox[] fileContainers = {uploadProfile, uploadGovernmentId};

        if (validator.hasEmptyFields(textFields, datePickers, comboBoxes)) return;

        if (civilStatusComboBox.getValue().equals(CivilStatus.MARRIED.getName()) && validator.hasEmptyFields(spouseTextField, null, spouseComboBox))
            return;

        if (validator.hasEmptyCheckBox(checkBoxes, checkBoxContainer)) {
            modalUtils.showModal(Modal.ERROR, "Failed", "Please select at least one source of income.");
            return;
        }

        if (validator.hasEmptyImages(images, fileContainers)) return;

        editResident();
    }

    private void setupActionButtons() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateForm());

        viewProfileBtn.setOnMouseClicked(_ -> showImageView(profilePreview.getImage()));
        viewGovernmentIdBtn.setOnMouseClicked(_ -> showImageView(governmentIdPreview.getImage()));
        viewTinId.setOnMouseClicked(_ -> showImageView(tinIdPreview.getImage()));

        uploadProfile.setOnMouseClicked(_ -> uploadImageUtils.loadSetupFile(currentStage, image -> {
            profilePreview.setImage(image);
            profileLabel.setText("Profile Picture");
            viewProfileBtn.setVisible(true);
            profileImage = image;
            profileLabel.setText(DateFormatter.toTimeStamp(ZonedDateTime.now()) + ".jpg");
            uploadProfile.setStyle(null);
        }));

        uploadGovernmentId.setOnMouseClicked(_ -> uploadImageUtils.loadSetupFile(currentStage, image -> {
            governmentIdPreview.setImage(image);
            viewGovernmentIdBtn.setVisible(true);
            governmentIdImage = image;
            governmentIdLabel.setText(DateFormatter.toTimeStamp(ZonedDateTime.now()) + ".jpg");
            uploadGovernmentId.setStyle(null);

        }));

        uploadTinId.setOnMouseClicked(_ -> uploadImageUtils.loadSetupFile(currentStage, image -> {
            tinIdPreview.setImage(image);
            viewTinId.setVisible(true);
            tinImage = image;
            governmentIdLabel.setText(DateFormatter.toTimeStamp(ZonedDateTime.now()) + ".jpg");
            uploadTinId.setStyle(null);
        }));

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
        modalUtils.closeCustomizeModal();
    }

    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();
        });
    }

    @Override
    public void setId(String id) {
        this.residentId = id;
    }
}