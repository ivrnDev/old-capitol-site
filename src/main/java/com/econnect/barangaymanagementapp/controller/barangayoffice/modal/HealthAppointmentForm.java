package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.domain.HealthAppointment;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.HealthType;
import com.econnect.barangaymanagementapp.service.HealthAppointmentService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.Validator;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.Response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HealthAppointmentForm {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn;
    @FXML
    private Button cancelBtn, confirmBtn;
    @FXML
    private TextField residentIdInput, lastNameInput, firstNameInput, middleNameInput, birthdateInput, sexInput, addressInput;
    @FXML
    private DatePicker appointmentDatePicker, birthdatePicker1;
    @FXML
    private ComboBox<String> servicesComboBox, providerComboBox, timeComboBox;
    @FXML
    private TextArea remarksInput;
    @FXML

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final Validator validator;
    private final HealthAppointmentService healthAppointmentService;
    private final ResidentService residentService;
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private boolean residentExists = false;

    public HealthAppointmentForm(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.healthAppointmentService = dependencyInjector.getHealthAppointmentService();
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

        Task<Response> addHealthAppointmentTask = new Task<>() {
            @Override
            protected Response call() {
                HealthAppointment appointment = createRequestsFromInput();
                return healthAppointmentService.createHealthAppointment(appointment);
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                closeWindow();

                if (getValue().isSuccessful())
                    modalUtils.showModal(Modal.SUCCESS, "Success", "Appointment has now been scheduled.");
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                Throwable exception = getException();
                exception.printStackTrace();
                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while submitting request.");
            }
        };

        new Thread(addHealthAppointmentTask).start();
    }

    private HealthAppointment createRequestsFromInput() {
        return HealthAppointment.builder()
                .id(residentIdInput.getText())
                .remarks(remarksInput.getText())
                .healthService(servicesComboBox.getValue())
                .healthCareProvider(providerComboBox.getValue())
                .appointmentDate(DateFormatter.formatLocalDateToUsShortDate(appointmentDatePicker.getValue()))
                .appointmentTime(timeComboBox.getValue())
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
                    lastNameInput.setText(residentInfo.getLastName());
                    firstNameInput.setText(residentInfo.getFirstName());
                    middleNameInput.setText(residentInfo.getMiddleName());
                    addressInput.setText(residentInfo.getAddress());
                    birthdateInput.setText(residentInfo.getBirthdate().toString());
                    sexInput.setText(residentInfo.getSex().getName());
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
        addressInput.clear();
        birthdateInput.clear();
        sexInput.clear();
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

        if (validator.hasEmptyFields(new TextArea[]{remarksInput}, new DatePicker[]{appointmentDatePicker}, new ComboBox[]{servicesComboBox, providerComboBox, timeComboBox}))
            return;

        submitData();
    }

    private void setupEventListener() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateData());

        validator.setupResidentIdInput(residentIdInput);
        residentIdInput.setOnKeyPressed(_ -> {
            residentIdInput.textProperty().addListener((_, _, newValue) -> {
                searchDelay.setOnFinished(_ -> populateInputFields(newValue));
                searchDelay.playFromStart();
            });
        });

        validator.setupDatePicker(LocalDate.now(), LocalDate.now().plusMonths(1), appointmentDatePicker);
        validator.setupComboBox(List.of(providerComboBox, servicesComboBox, timeComboBox));

        providerComboBox.getItems().addAll(Arrays.stream(HealthType.HealthcareProvider.values()).map(HealthType.HealthcareProvider::getName).toList());
        servicesComboBox.getItems().addAll(Arrays.stream(HealthType.HealthServiceType.values()).map(HealthType.HealthServiceType::getName).toList());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm");
        List<LocalTime> times = Stream.iterate(LocalTime.of(9, 0), time -> time.plusMinutes(30))
                .limit(13)
                .collect(Collectors.toList());
        timeComboBox.getItems().addAll(times.stream().map(time -> time.format(timeFormatter)).toList());
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
