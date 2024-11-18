package com.econnect.barangaymanagementapp.controller.shared.modal;

import com.econnect.barangaymanagementapp.controller.component.BaseViewController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

public class ViewEmployeeApplicationController implements BaseViewController {
    @FXML
    private ImageView closeBtn, profilePicture, validIdPreview, NBIPreview, resumePreview;
    @FXML
    private TextField residentIdInput, lastNameInput, firstNameInput, middleNameInput, nameExtensionInput, addressInput, birthdateInput, birthPlaceInput,
            emailInput, contactNumberInput, telephoneNumberInput, citizenshipInput, civilStatusInput, motherToungeInput, bloodTypeInput, religionInput, incomeInput,
            residencyStatusInput, educationAttainmentInput, spouseLastNameInput, spouseFirstNameInput, spouseMiddleNameInput, spouseNameExtensionInput, spouseOccupationInput,
            fatherLastNameInput, fatherFirstNameInput, fatherMiddleNameInput, fatherNameExtensionInput, fatherOccupationInput, motherLastNameInput, motherFirstNameInput, motherMiddleNameInput,
            motherNameExtensionInput, motherOccupationInput;
    @FXML
    private HBox profileContainer, viewValidId, viewValidIdPreviewContainer, viewNBI, viewNBIPreviewContainer, viewResumePreviewContainer, viewResume;
    @FXML
    private VBox spouseContainer;
    @FXML
    private Text validIdExpiryDateText, NBIExpiryDateText;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final ImageService imageService;
    private final ResidentService residentService;
    private final EmployeeService employeeService;
    private Image profileImage;
    private Image governmentIdImage;
    private Image NBIImage;
    private Image resumeImage;
    private String residentId;


    public ViewEmployeeApplicationController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.imageService = dependencyInjector.getImageService();
        this.employeeService = dependencyInjector.getEmployeeService();
        Platform.runLater(() -> currentStage = (Stage) closeBtn.getScene().getWindow());
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeView());
        setupPreviewRounded();
        fetchData();
    }

    private void setupSpouseContainer(ResidentInfomationType.CivilStatus civilStatus) {
        if (civilStatus.equals(ResidentInfomationType.CivilStatus.MARRIED)) {
            spouseContainer.setVisible(true);
            spouseContainer.setManaged(true);
            return;
        }
        spouseContainer.setVisible(false);
        spouseContainer.setManaged(false);
    }

    private void fetchData() {
        Task<Optional<Resident>> residentTask = new Task<>() {
            @Override
            protected Optional<Resident> call() {
                return residentService.findResidentById(residentId);
            }

            @Override
            protected void succeeded() {
                Optional<Resident> residentValue = getValue();
                if (residentValue.isPresent()) {
                    Resident resident = residentValue.get();
                    populateData(resident);
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), resident.getProfileUrl());
                    loadGovernmentIdImage(Firestore.VALID_ID.getPath(), resident.getValidIdUrl());
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        Task<Optional<Employee>> findEmployeeURL = new Task<>() {
            @Override
            protected Optional<Employee> call() {
                return employeeService.findEmployeeById(residentId);
            }

            @Override
            protected void succeeded() {
                Optional<Employee> fetchedEmployee = getValue();
                fetchedEmployee.ifPresent(employee -> {
                    loadResumeImage(Firestore.RESUME.getPath(), employee.getResumeUrl());
                    loadNBIIImage(Firestore.NBI_CLEARANCE.getPath(), employee.getNbiClearanceUrl());
                    NBIExpiryDateText.setText(DateFormatter.formatShortToLongDate(employee.getNbiClearanceExpiration()));
                });
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };
        new Thread(residentTask).start();
        new Thread(findEmployeeURL).start();
    }

    private void populateData(Resident resident) {
        if (resident == null) return;
        residentIdInput.setText(resident.getId());
        lastNameInput.setText(resident.getLastName());
        firstNameInput.setText(resident.getFirstName());
        middleNameInput.setText(resident.getMiddleName());
        nameExtensionInput.setText(!resident.getNameExtension().isEmpty() ? resident.getNameExtension() : ResidentInfomationType.SuffixName.NONE.getName());
        addressInput.setText(resident.getAddress());
        birthdateInput.setText(DateFormatter.formatToLongDate(resident.getBirthdate()));
        birthPlaceInput.setText(resident.getBirthplace());
        emailInput.setText(resident.getEmail());
        contactNumberInput.setText(resident.getMobileNumber());
        telephoneNumberInput.setText(resident.getTelephoneNumber());
        citizenshipInput.setText(resident.getCitizenship());
        civilStatusInput.setText(resident.getCivilStatus().getName());
        motherToungeInput.setText(resident.getMotherTounge());
        bloodTypeInput.setText(resident.getBloodType().getName());
        religionInput.setText(resident.getReligion());
        incomeInput.setText(resident.getSourceOfIncome());
        residencyStatusInput.setText(resident.getResidencyStatus().getName());
        educationAttainmentInput.setText(resident.getEducationalAttainment());
        spouseLastNameInput.setText(resident.getSpouseLastName());
        spouseFirstNameInput.setText(resident.getSpouseFirstName());
        spouseMiddleNameInput.setText(resident.getSpouseMiddleName());
        spouseNameExtensionInput.setText(resident.getSpouseSuffixName());
        spouseOccupationInput.setText(resident.getSpouseOccupation());
        fatherLastNameInput.setText(resident.getFatherLastName());
        fatherFirstNameInput.setText(resident.getFatherFirstName());
        fatherMiddleNameInput.setText(resident.getFatherMiddleName());
        fatherNameExtensionInput.setText(!resident.getFatherSuffixName().isEmpty() ? resident.getFatherSuffixName() : ResidentInfomationType.SuffixName.NONE.getName());
        fatherOccupationInput.setText(resident.getFatherOccupation());
        motherLastNameInput.setText(resident.getMotherLastName());
        motherFirstNameInput.setText(resident.getMotherFirstName());
        motherMiddleNameInput.setText(resident.getMotherMiddleName());
        motherNameExtensionInput.setText(!resident.getMotherSuffixName().isEmpty() ? resident.getMotherSuffixName() : ResidentInfomationType.SuffixName.NONE.getName());
        motherOccupationInput.setText(resident.getMotherOccupation());
        validIdExpiryDateText.setText(DateFormatter.formatShortToLongDate(resident.getValidIdExpiration()));
        setupSpouseContainer(resident.getCivilStatus());
    }

    private void loadProfileImage(String directory, String link) {
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), currentStage));
        profilePicture.setCursor(Cursor.HAND);
        profilePicture.setManaged(false);
        profilePicture.setVisible(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(profileContainer.getWidth(), profileContainer.getHeight());
        Platform.runLater(() -> profileContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            profileImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                profileContainer.getChildren().remove(loadingIndicator);
                profilePicture.setImage(profileImage);
                profilePicture.setManaged(true);
                profilePicture.setVisible(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> profileContainer.getChildren().remove(loadingIndicator));
            profilePicture.setManaged(true);
            profilePicture.setVisible(true);
            System.err.println("Error loading resident");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void loadGovernmentIdImage(String directory, String link) {
        viewValidId.setOnMouseClicked(_ -> {
            if (governmentIdImage != null) {
                modalUtils.showImageView(governmentIdImage, currentStage);
            }
        });
        validIdPreview.setVisible(false);
        validIdPreview.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(viewValidIdPreviewContainer.getWidth(), viewValidIdPreviewContainer.getHeight());
        Platform.runLater(() -> viewValidIdPreviewContainer.getChildren().add(loadingIndicator));
        Runnable call = () -> {
            governmentIdImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                viewValidIdPreviewContainer.getChildren().remove(loadingIndicator);
                validIdPreview.setImage(governmentIdImage);
                viewValidId.setCursor(Cursor.HAND);
                validIdPreview.setVisible(true);
                validIdPreview.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> {
                viewValidIdPreviewContainer.getChildren().remove(loadingIndicator);

            });
            validIdPreview.setVisible(true);
            validIdPreview.setManaged(true);

            System.err.println("Error loading image");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void loadNBIIImage(String directory, String link) {
        viewNBI.setOnMouseClicked(_ -> {
            if (NBIImage != null) {
                modalUtils.showImageView(NBIImage, currentStage);
            }
        });
        NBIPreview.setVisible(false);
        NBIPreview.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(viewNBIPreviewContainer.getWidth(), viewNBIPreviewContainer.getHeight());
        Platform.runLater(() -> viewNBIPreviewContainer.getChildren().add(loadingIndicator));
        Runnable call = () -> {
            NBIImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                viewNBIPreviewContainer.getChildren().remove(loadingIndicator);
                NBIPreview.setImage(NBIImage);
                viewNBI.setCursor(Cursor.HAND);
                NBIPreview.setVisible(true);
                NBIPreview.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> {
                viewNBIPreviewContainer.getChildren().remove(loadingIndicator);
            });
            NBIPreview.setVisible(true);
            NBIPreview.setManaged(true);

            System.err.println("Error loading image");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void loadResumeImage(String directory, String link) {
        viewResume.setOnMouseClicked(_ -> {
            if (resumeImage != null) {
                modalUtils.showImageView(resumeImage, currentStage);
            }
        });
        resumePreview.setVisible(false);
        resumePreview.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(viewResumePreviewContainer.getWidth(), viewResumePreviewContainer.getHeight());
        Platform.runLater(() -> viewResumePreviewContainer.getChildren().add(loadingIndicator));
        Runnable call = () -> {
            resumeImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                viewResumePreviewContainer.getChildren().remove(loadingIndicator);
                resumePreview.setImage(resumeImage);
                viewResume.setCursor(Cursor.HAND);
                resumePreview.setVisible(true);
                resumePreview.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> {
                viewResumePreviewContainer.getChildren().remove(loadingIndicator);
            });
            resumePreview.setVisible(true);
            resumePreview.setManaged(true);

            System.err.println("Error loading image");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void setupPreviewRounded() {
        ImageUtils.setRoundedClip(profilePicture, 25, 25);
        ImageUtils.setRoundedClip(NBIPreview, 10, 10);
        ImageUtils.setRoundedClip(resumePreview, 10, 10);
        ImageUtils.setRoundedClip(validIdPreview, 10, 10);
    }

    private void closeView() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.residentId = id;
    }
}
