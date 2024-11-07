package com.econnect.barangaymanagementapp.controller.shared.modal;

import com.econnect.barangaymanagementapp.controller.component.BaseViewController;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType;
import com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.CivilStatus;
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

public class ViewResidentController implements BaseViewController {
    @FXML
    private ImageView closeBtn, profilePicture, governmentIdPreview, tinIdPreview;
    @FXML
    private TextField residentIdInput, lastNameInput, firstNameInput, middleNameInput, nameExtensionInput, addressInput, birthdateInput, birthPlaceInput,
            emailInput, contactNumberInput, telephoneNumberInput, citizenshipInput, civilStatusInput, motherToungeInput, bloodTypeInput, religionInput, incomeInput,
            residencyStatusInput, educationAttainmentInput, spouseLastNameInput, spouseFirstNameInput, spouseMiddleNameInput, spouseNameExtensionInput, spouseOccupationInput,
            fatherLastNameInput, fatherFirstNameInput, fatherMiddleNameInput, fatherNameExtensionInput, fatherOccupationInput, motherLastNameInput, motherFirstNameInput, motherMiddleNameInput,
            motherNameExtensionInput, motherOccupationInput;
    @FXML
    private HBox profileContainer, viewGovernmentID, governmentIdPreviewContainer, viewTinId, viewTinIdPreviewContainer;
    @FXML
    private VBox spouseContainer;
    @FXML
    private Text validIdExpiryDateText;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final ImageService imageService;
    private final ResidentService residentService;
    private Image profileImage;
    private Image governmentIdImage;
    private Image tinIdImage;
    private String residentId;


    public ViewResidentController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.currentStage = dependencyInjector.getStage();
        this.imageService = dependencyInjector.getImageService();
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeView());
        setupPreviewRounded();
        populateFields();
    }

    private void setupSpouseContainer(CivilStatus civilStatus) {
        if (civilStatus.equals(CivilStatus.MARRIED)) {
            spouseContainer.setVisible(true);
            spouseContainer.setManaged(true);
            return;
        }
        spouseContainer.setVisible(false);
        spouseContainer.setManaged(false);
    }

    private void populateFields() {
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
                    loadTinIdImage(Firestore.TIN_ID.getPath(), resident.getTinIdUrl());
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };
        new Thread(residentTask).start();
    }

    private void populateData(Resident resident) {
        if (resident == null) return;
        residentIdInput.setText(resident.getId());
        lastNameInput.setText(resident.getLastName());
        firstNameInput.setText(resident.getFirstName());
        middleNameInput.setText(resident.getMiddleName());
        nameExtensionInput.setText(!resident.getNameExtension().isEmpty() ? resident.getNameExtension() : ResidentInfomationType.SuffixName.NONE.getName());
        addressInput.setText(resident.getAddress());
        birthdateInput.setText(resident.getBirthdate().toString());
        birthPlaceInput.setText(resident.getBirthplace());
        emailInput.setText(resident.getEmail());
        contactNumberInput.setText(resident.getMobileNumber());
        telephoneNumberInput.setText(resident.getTelephoneNumber());
        citizenshipInput.setText(resident.getCitizenship());
        civilStatusInput.setText(resident.getCivilStatus().getName());
        motherToungeInput.setText(resident.getMotherTounge().getName());
        bloodTypeInput.setText(resident.getBloodType().getName());
        religionInput.setText(resident.getReligion().getName());
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
        validIdExpiryDateText.setText(DateFormatter.toNamedFormat(resident.getValidIdExpiration()));
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
        viewGovernmentID.setOnMouseClicked(_ -> {
            if (governmentIdImage != null) {
                modalUtils.showImageView(governmentIdImage, currentStage);
            }
        });
        governmentIdPreview.setVisible(false);
        governmentIdPreview.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(governmentIdPreviewContainer.getWidth(), governmentIdPreviewContainer.getHeight());
        Platform.runLater(() -> governmentIdPreviewContainer.getChildren().add(loadingIndicator));
        Runnable call = () -> {
            governmentIdImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                governmentIdPreviewContainer.getChildren().remove(loadingIndicator);
                governmentIdPreview.setImage(governmentIdImage);
                viewGovernmentID.setCursor(Cursor.HAND);
                governmentIdPreview.setVisible(true);
                governmentIdPreview.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> {
                governmentIdPreviewContainer.getChildren().remove(loadingIndicator);

            });
            governmentIdPreview.setVisible(true);
            governmentIdPreview.setManaged(true);

            System.err.println("Error loading image");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void loadTinIdImage(String directory, String link) {
        viewTinId.setOnMouseClicked(_ -> {
            if (tinIdImage != null) {
                modalUtils.showImageView(tinIdImage, currentStage);
            }
        });
        tinIdPreview.setVisible(false);
        tinIdPreview.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(viewTinIdPreviewContainer.getWidth(), viewTinIdPreviewContainer.getHeight());
        Platform.runLater(() -> viewTinIdPreviewContainer.getChildren().add(loadingIndicator));
        Runnable call = () -> {
            tinIdImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                viewTinIdPreviewContainer.getChildren().remove(loadingIndicator);
                tinIdPreview.setImage(tinIdImage);
                viewTinId.setCursor(Cursor.HAND);
                tinIdPreview.setVisible(true);
                tinIdPreview.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> {
                viewTinIdPreviewContainer.getChildren().remove(loadingIndicator);
            });
            tinIdPreview.setVisible(true);
            tinIdPreview.setManaged(true);

            System.err.println("Error loading image");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void setupPreviewRounded() {
        ImageUtils.setRoundedClip(profilePicture, 25, 25);
        ImageUtils.setRoundedClip(tinIdPreview, 10, 10);
        ImageUtils.setRoundedClip(governmentIdPreview, 10, 10);
    }

    private void closeView() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.residentId = id;
    }
}
