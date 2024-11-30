package com.econnect.barangaymanagementapp.controller.form;

import com.econnect.barangaymanagementapp.domain.Borrow;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.service.BorrowService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.InventoryService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.Validator;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.Response;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BorrowController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn, imagePreview;
    @FXML
    private Button cancelBtn, confirmBtn;
    @FXML
    private TextField residentIdInput, quantityInput, borrowerNameInput;
    @FXML
    private ComboBox<String> itemComboBox;
    @FXML
    private DatePicker returnedDatePicker;
    @FXML
    private TextArea purposeInput;
    @FXML
    private HBox imageContainer;
    @FXML
    private Slider slider;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final Validator validator;
    private final BorrowService borrowService;
    private final ResidentService residentService;
    private final InventoryService inventoryService;
    private final ImageService imageService;
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private boolean residentExists = false;
    private Image image;

    public BorrowController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.borrowService = dependencyInjector.getBorrowService();
        this.residentService = dependencyInjector.getResidentService();
        this.inventoryService = dependencyInjector.getInventoryService();
        this.validator = dependencyInjector.getValidator();
        this.imageService = dependencyInjector.getImageService();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupEventListener();
        populateItemComboBox();
    }

    private void submitData() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Task<Response> addBorrowTask = new Task<>() {
            @Override
            protected Response call() {
                Borrow borrow = createRequestsFromInput();
                return borrowService.createBorrow(borrow);
            }

            @Override
            protected void succeeded() {
                if (getValue().isSuccessful()) {
                    closeWindow();
                    modalUtils.showModal(Modal.SUCCESS, "Success", "Borrow request submitted successfully.");
                } else {
                    modalUtils.showModal(Modal.ERROR, "Failed", "Failed to submit Borrow request.");
                }
            }

            @Override
            protected void failed() {
                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while submitting Borrow request.");
            }
        };


        new Thread(addBorrowTask).start();
    }

    private Borrow createRequestsFromInput() {
        return Borrow.builder()
                .id(residentIdInput.getText())
                .itemId(itemComboBox.getValue().split("-")[0].trim())
                .quantity(quantityInput.getText())
                .purpose(purposeInput.getText())
                .returnedDate(DateFormatter.formatLocalDateToUsShortDate(returnedDatePicker.getValue()))
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
                    borrowerNameInput.setText(fullName);
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

    private void populateItemComboBox() {
        Task<List<Inventory>> getItems = new Task<>() {
            @Override
            protected List<Inventory> call() {
                return inventoryService.findAllPublicInventories();
            }

            @Override
            protected void succeeded() {
                List<Inventory> items = getValue();
                List<String> itemNames = items.stream().map(item -> item.getId() + "" + "-" + "" + item.getItemName()).collect(Collectors.toList());
                itemComboBox.getItems().addAll(itemNames);
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        new Thread(getItems).start();
    }

    private void clearInputFields() {
        borrowerNameInput.clear();
    }

    private void validateData() {
        TextField[] textFields = {borrowerNameInput, quantityInput};
        TextArea[] textAreas = {purposeInput};
        ComboBox[] comboBoxes = {itemComboBox};
        DatePicker[] datePickers = {returnedDatePicker};

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

        if (validator.hasEmptyFields(textFields, textAreas, datePickers, comboBoxes)) return;


        submitData();
    }

    private void loadItemImage(String directory, String link) {
        imagePreview.setOnMouseClicked(_ -> modalUtils.showImageView(imagePreview.getImage(), currentStage));
        imagePreview.setCursor(Cursor.HAND);
        imagePreview.setVisible(false);
        imagePreview.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(imageContainer.getWidth(), imageContainer.getHeight());
        Platform.runLater(() -> imageContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            image = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                imageContainer.getChildren().remove(loadingIndicator);
                imagePreview.setImage(image);
                imagePreview.setVisible(true);
                imagePreview.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> {
                imageContainer.getChildren().remove(loadingIndicator);
                imagePreview.setVisible(true);
                imagePreview.setManaged(true);
            });
            System.err.println("Error loading profile image");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void setupEventListener() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateData());

        residentIdInput.setOnKeyPressed(_ -> {
            residentIdInput.textProperty().addListener((_, _, newValue) -> {
                searchDelay.setOnFinished(_ -> populateInputFields(newValue));
                searchDelay.playFromStart();
            });
        });
        validator.setupResidentIdInput(residentIdInput);
        itemComboBox.setOnAction(_ -> {
            String item = itemComboBox.getValue();
            if (item != null) {
                String itemId = item.split("-")[0].trim();
                Inventory inventory = inventoryService.findInventoryById(itemId).orElse(null);
                if (inventory != null) {
                    Platform.runLater(() -> loadItemImage(Firestore.ITEM.getPath(), inventory.getItemImageUrl()));
                }

                if (inventory != null) {
                    double stocks = Double.parseDouble(inventory.getStocks());
                    slider.setMax(stocks);
                    slider.setBlockIncrement(stocks / 10);
                    slider.setMajorTickUnit(stocks / 5);
                    slider.valueProperty().addListener((_, _, newValue) -> quantityInput.setText(String.valueOf(newValue.intValue())));
                    quantityInput.setEditable(true);
                    quantityInput.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue.isEmpty()) {
                            try {
                                double value = Double.parseDouble(newValue);
                                if (value > stocks) {
                                    quantityInput.setText(oldValue);
                                } else {
                                    slider.setValue(value);
                                }
                            } catch (NumberFormatException e) {
                                quantityInput.setText(oldValue);
                            }
                        }
                    });
                }
            }
        });
        validator.setupComboBox(itemComboBox);

        LocalDate minDate = LocalDate.now().plusDays(1);
        LocalDate maxDate = LocalDate.now().plusMonths(6);
        validator.setupDatePicker(minDate, maxDate, returnedDatePicker);
        validator.createNumberFormatter(new TextField[]{quantityInput});
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
