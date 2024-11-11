package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.domain.Cedula;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.service.CedulaService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.Validator;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;

public class CedulaFormController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn;
    @FXML
    private Button cancelBtn, confirmBtn;
    @FXML
    private TextField residentIdInput, nameInput, addressInput, sexInput, citizenshipInput, civilStatusInput, birthdateInput, heightInput, weightInput,
            occupationInput, tinNumberInput, grossReceiptInput, totalEarningsInput;
    @FXML
    private TextArea purposeInput;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final Validator validator;
    private final CedulaService cedulaService;
    private final ResidentService residentService;
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private boolean residentExists = false;

    public CedulaFormController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.cedulaService = dependencyInjector.getCedulaService();
        this.residentService = dependencyInjector.getResidentService();
        this.validator = dependencyInjector.getValidator();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupEventListener();
    }

    private void submitData() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Task<Response> addCedulaTask = new Task<>() {
            @Override
            protected Response call() {
                Cedula cedula = createRequestsFromInput();
                return cedulaService.createCedula(cedula);
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                closeWindow();

                if (getValue().isSuccessful())
                    modalUtils.showModal(Modal.SUCCESS, "Success", "Cedula Request has been successfully added.");
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while submitting request.");
            }
        };

        new Thread(addCedulaTask).start();
    }

    private Cedula createRequestsFromInput() {
        return Cedula.builder()
                .id(residentIdInput.getText())
                .grossReceipt(grossReceiptInput.getText())
                .totalEarnings(totalEarningsInput.getText())
                .height(heightInput.getText())
                .weight(weightInput.getText())
                .build();
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
                return residentService.findVerifiedResidentById(residentId);
            }

            @Override
            protected void succeeded() {
                Optional<Resident> resident = getValue();
                if (resident.isPresent()) {
                    residentExists = true;
                    Resident residentInfo = resident.get();
                    String firstName = residentInfo.getFirstName();
                    String lastName = residentInfo.getLastName();
                    String middleName = residentInfo.getMiddleName();
                    String fullName = lastName + ", " + firstName + " " + middleName;
                    nameInput.setText(fullName);
                    addressInput.setText(residentInfo.getAddress());
                    birthdateInput.setText(residentInfo.getBirthdate().toString());
                    occupationInput.setText(residentInfo.getOccupation());
                    sexInput.setText(residentInfo.getSex().getName());
                    citizenshipInput.setText(residentInfo.getCitizenship());
                    civilStatusInput.setText(residentInfo.getCivilStatus().getName());
                    tinNumberInput.setText(residentInfo.getTinIdNumber());
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
        nameInput.clear();
        addressInput.clear();
        birthdateInput.clear();
        occupationInput.clear();
        sexInput.clear();
        citizenshipInput.clear();
        civilStatusInput.clear();
        tinNumberInput.clear();
    }

    private void validateData() {

        if (validator.validate(residentIdInput, Validator.VALIDATOR_TYPE.IS_EMPTY)) {
            residentIdInput.requestFocus();
            residentIdInput.setStyle("-fx-border-color: red;");
            modalUtils.showModal(Modal.ERROR, "Empty Resident ID", "Please enter a Resident ID.");
            return;
        } else {
            residentIdInput.setStyle(null);
        }

        if (!residentExists) {
            modalUtils.showModal(Modal.ERROR, "Resident Not Found", "Resident ID does not exist.");
            return;
        }

        if (validator.hasEmptyFields(List.of(grossReceiptInput, totalEarningsInput, heightInput, weightInput), List.of(purposeInput)))
            return;

        submitData();
    }

    private void setupEventListener() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateData());

        validator.createHeightFormatter(heightInput);
        validator.createWeightFormatter(weightInput);
        validator.setupResidentIdInput(residentIdInput);
        residentIdInput.setOnKeyPressed(_ -> {
            residentIdInput.textProperty().addListener((_, _, newValue) -> {
                searchDelay.setOnFinished(_ -> populateInputFields(newValue));
                searchDelay.playFromStart();
            });
        });
        validator.setupCurrencyListener(grossReceiptInput, totalEarningsInput);
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }

    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();
        });
    }
}
