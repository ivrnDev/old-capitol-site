package com.econnect.barangaymanagementapp.controller.form;

import com.econnect.barangaymanagementapp.domain.Event;
import com.econnect.barangaymanagementapp.domain.EventItems;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.service.EventItemsService;
import com.econnect.barangaymanagementapp.service.EventService;
import com.econnect.barangaymanagementapp.service.InventoryService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.Validator;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import okhttp3.Response;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventFormController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn;
    @FXML
    private Button cancelBtn, confirmBtn, addBtn, minusBtn;
    @FXML
    private TextField residentIdInput, residentNameInput;
    @FXML
    private ComboBox<String> typeComboBox, placeComboBox, timeComboBox;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private TextArea purposeInput;
    @FXML
    private VBox itemContainer;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final Validator validator;
    private final EventService eventservice;
    private final ResidentService residentService;
    private final InventoryService inventoryService;
    private final EventItemsService eventItemsService;
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private boolean residentExists = false;
    private List<String> cachedItemNames;

    public EventFormController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.eventservice = dependencyInjector.getEventService();
        this.residentService = dependencyInjector.getResidentService();
        this.inventoryService = dependencyInjector.getInventoryService();
        this.eventItemsService = dependencyInjector.getEventItemsService();
        this.validator = dependencyInjector.getValidator();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupEventListener();
    }

    private void submitData() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Task<Response> addEventTask = new Task<>() {
            @Override
            protected Response call() {
                Event event = createEventRequestFromInput();

                return eventservice.createEvent(event, getAllEventItems());
            }

            @Override
            protected void succeeded() {
                if (getValue().isSuccessful()) {
                    closeWindow();
                    modalUtils.showModal(Modal.SUCCESS, "Success", "Event request submitted successfully.");
                } else {
                    modalUtils.showModal(Modal.ERROR, "Failed", "Failed to submit Event request.");
                }
            }

            @Override
            protected void failed() {
                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while submitting Event request.");
            }
        };


        new Thread(addEventTask).start();
    }

    private Event createEventRequestFromInput() {
        return Event.builder()
                .id(residentIdInput.getText())
                .requestorId(residentIdInput.getText())
                .eventType(typeComboBox.getValue())
                .eventPlace(placeComboBox.getValue())
                .eventTime(timeComboBox.getValue())
                .eventDate(eventDatePicker.getValue().toString())
                .purpose(purposeInput.getText())
                .build();
    }

    private void populateInputFields(String residentId) {
        residentExists = false;
        if (residentId.isEmpty()) {
            clearInputFields();
            return;
        }

        Task<Optional<Resident>> getResident = new Task<>() {
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
                    residentNameInput.setText(fullName);
                } else {
                    clearInputFields();
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        new Thread(getResident).start();
    }

    private void clearInputFields() {
        residentNameInput.clear();
    }

    private void validateData() {
        TextField[] textFields = null;
        TextArea[] textAreas = {purposeInput};
        ComboBox[] comboBoxes = {placeComboBox, typeComboBox, timeComboBox};
        DatePicker[] datePickers = {eventDatePicker};

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

//        if (validator.hasEmptyFields(textFields, textAreas, datePickers, comboBoxes)) return;


        submitData();
    }

    private void setupEventListener() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateData());
        addBtn.setOnAction(_ -> addItemRow());

        residentIdInput.setOnKeyPressed(_ -> {
            residentIdInput.textProperty().addListener((_, _, newValue) -> {
                searchDelay.setOnFinished(_ -> populateInputFields(newValue));
                searchDelay.playFromStart();
            });
        });
        validator.setupResidentIdInput(residentIdInput);
        LocalDate minDate = LocalDate.now();
        LocalDate maxDate = LocalDate.now().plusMonths(6);
        validator.setupDatePicker(minDate, maxDate, eventDatePicker);
        populateComboBoxes();
    }

    private void populateComboBoxes() {
        typeComboBox.getItems().addAll(Arrays.stream(EventType.values()).map(EventType::getName).collect(Collectors.toList()));
        placeComboBox.getItems().addAll(Arrays.stream(EventPlace.values()).map(EventPlace::getName).collect(Collectors.toList()));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm");
        List<LocalTime> times = Stream.iterate(LocalTime.of(8, 0), time -> time.plusMinutes(60))
                .limit(27)
                .collect(Collectors.toList());
        timeComboBox.getItems().addAll(times.stream().map(time -> time.format(timeFormatter)).toList());
    }

    private void addItemRow() {
        HBox itemRow = new HBox(10);
        itemRow.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> itemComboBox = new ComboBox<>();
        itemComboBox.setPrefHeight(35.0);
        itemComboBox.setPrefWidth(164.0);
        itemComboBox.setMinHeight(35.0);
        itemComboBox.setMinHeight(35.0);
        itemComboBox.setPromptText("Select item");
        itemComboBox.getStyleClass().add("text-field");
        populateItemComboBox(itemComboBox);

        TextField quantityInput = new TextField();
        quantityInput.setPrefHeight(35.0);
        quantityInput.setPrefWidth(71.0);
        quantityInput.getStyleClass().add("text-input-default");
        quantityInput.setFont(new Font("Arial", 14.0));
        quantityInput.setCursor(Cursor.DEFAULT);
        itemComboBox.setOnAction(_ -> updateQuantityInput(itemComboBox, quantityInput));

        Button minusBtn = new Button("-");
        minusBtn.setPrefHeight(31.0);
        minusBtn.setPrefWidth(46.0);
        minusBtn.setText("-");
        minusBtn.setFont(new Font(14.0));
        minusBtn.setPadding(new Insets(0, 20, 0, 20));
        minusBtn.setCursor(Cursor.HAND);
        minusBtn.getStyleClass().addAll("drop-shadow", "h-reject");
        minusBtn.setOnAction(e -> {
            itemContainer.getChildren().remove(itemRow);
            addBtn.setDisable(false);
        });

        itemRow.getChildren().addAll(itemComboBox, quantityInput, minusBtn);
        itemContainer.getChildren().add(itemRow);

        if (cachedItemNames != null && itemContainer.getChildren().size() >= cachedItemNames.size()) {
            addBtn.setDisable(true);
        }
    }

    private void populateItemComboBox(ComboBox<String> comboBox) {
        if (cachedItemNames != null) {
            comboBox.getItems().addAll(cachedItemNames);
            return;
        }

        Task<List<Inventory>> getItems = new Task<>() {
            @Override
            protected List<Inventory> call() {
                return inventoryService.findAllPublicInventories();
            }

            @Override
            protected void succeeded() {
                List<Inventory> items = getValue();
                cachedItemNames = items.stream()
                        .map(item -> item.getId() + "-" + item.getItemName())
                        .collect(Collectors.toList());
                comboBox.getItems().addAll(cachedItemNames);
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        new Thread(getItems).start();
    }

    private void updateQuantityInput(ComboBox<String> itemComboBox, TextField quantityInput) {
        String selectedItem = itemComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String itemId = selectedItem.split("-")[0];
            Task<Optional<Inventory>> getItemStock = new Task<>() {
                @Override
                protected Optional<Inventory> call() {
                    return inventoryService.findInventoryById(itemId);
                }

                @Override
                protected void succeeded() {
                    Optional<Inventory> fetchedItem = getValue();
                    if (fetchedItem.isEmpty()) return;
                    int stock = Integer.parseInt(fetchedItem.get().getStocks());
                    quantityInput.setPromptText("0 / " + stock);
                    quantityInput.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue.matches("\\d*")) {
                            quantityInput.setText(newValue.replaceAll("[^\\d]", ""));
                        } else if (!newValue.isEmpty() && (Integer.parseInt(newValue) > stock || newValue.matches("0\\d+"))) {
                            quantityInput.setText(oldValue);
                        }
                    });
                }

                @Override
                protected void failed() {
                    System.out.println("Failed to fetch stock: " + getException().getMessage());
                }
            };

            new Thread(getItemStock).start();
        }
    }

    private List<EventItems> getAllEventItems() {
        List<EventItems> eventItemsList = new ArrayList<>();

        for (Node node : itemContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox itemRow = (HBox) node;
                ComboBox<String> itemComboBox = (ComboBox<String>) itemRow.getChildren().get(0);
                TextField quantityInput = (TextField) itemRow.getChildren().get(1);

                String selectedItem = itemComboBox.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    String[] itemParts = selectedItem.split("-");
                    String itemId = itemParts[0];
                    String itemName = itemParts[1];
                    String quantity = quantityInput.getText();

                    EventItems eventItem = EventItems.builder()
                            .itemId(itemId)
                            .itemName(itemName)
                            .quantity(quantity)
                            .build();

                    eventItemsList.add(eventItem);
                }
            }
        }

        return eventItemsList;
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }

    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();
        });
    }

    @Getter
    public enum EventType {
        BIRTHDAY("Birthday"),
        WEDDING("Wedding"),
        FUNERAL("Funeral"),
        SPORTS("Sports"),
        MEETING("Meeting"),
        OTHERS("Others");
        private String name;

        EventType(String name) {
            this.name = name;
        }

        public static EventType fromName(String name) {
            for (EventType status : EventType.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }

    }

    @Getter
    public enum EventPlace {
        BARANGAY_HALL("Barangay Hall"),
        COVERED_COURT("Covered Court"),
        OTHER("Other");

        private String name;

        EventPlace(String name) {
            this.name = name;
        }

        public static EventPlace fromName(String name) {
            for (EventPlace status : EventPlace.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }

    }

}
