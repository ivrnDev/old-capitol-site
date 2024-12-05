package com.econnect.barangaymanagementapp.controller.form;

import com.econnect.barangaymanagementapp.controller.base.BaseViewController;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.ItemAvailability;
import com.econnect.barangaymanagementapp.enumeration.type.Itemtype;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.InventoryStatus;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.InventoryService;
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
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EditItemController implements BaseViewController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView closeBtn, itemImage;
    @FXML
    private TextField stockInput, itemNameInput, maxStockInput, minStockInput;
    @FXML
    private ComboBox<String> itemTypeComboBox, availabilityComboBox;
    @FXML
    private Label itemLabel;
    @FXML
    private HBox uploadItemImage, itemImageContainer;
    @FXML
    private Slider minSlider, maxSlider, stockSlider;
    @FXML
    private Button cancelBtn, confirmBtn;

    private Stage currentStage;
    private final ModalUtils modalUtils;
    private final InventoryService inventoryService;
    private final ImageService imageService;
    private final Validator validator;
    private final UploadImageUtils uploadImageUtils;
    private Image image;
    private String itemId;

    public EditItemController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.inventoryService = dependencyInjector.getInventoryService();
        this.imageService = dependencyInjector.getImageService();
        this.validator = dependencyInjector.getValidator();
        this.uploadImageUtils = dependencyInjector.getUploadImageUtils();
        Platform.runLater(() -> currentStage = (Stage) rootPane.getScene().getWindow());
    }

    public void initialize() {
        setupEventListeners();
        populateFields();
        ImageUtils.setRoundedClip(itemImage, 25, 25);
    }

    private void addItem() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().add(loadingIndicator);

        Inventory inventory = createInventoryFromInputs();

        Task<Void> addInventoryTask = new Task<>() {
            @Override
            protected Void call() {
                return processInventoryEdit(inventory);
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                closeWindow();
                modalUtils.showModal(Modal.SUCCESS, "Success", "Item with ID " + itemId + " has been updated successfully.");
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false);
                rootPane.getChildren().remove(loadingIndicator);
                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while adding the inventory.");
            }
        };

        new Thread(addInventoryTask).start();
    }

    private Inventory createInventoryFromInputs() {
        return Inventory.builder()
                .itemName(itemNameInput.getText())
                .itemType(itemTypeComboBox.getValue())
                .availability(availabilityComboBox.getValue())
                .stocks(stockInput.getText())
                .minStock(minStockInput.getText())
                .maxStock(maxStockInput.getText())
                .status(InventoryStatus.AVAILABLE.getName())
                .build();
    }

    private Void processInventoryEdit(Inventory inventory) {
        if (image != null) {
            String itemUrl = imageService.uploadImage(Firestore.ITEM, image, itemId.toString());
            inventory.setItemImageUrl(itemUrl);
        }
        inventory.setId(itemId);
        inventoryService.updateInventory(inventory);
        return null;
    }

    private void populateFields() {
        Task<Optional<Inventory>> populateFieldsTask = new Task<>() {
            @Override
            protected Optional<Inventory> call() {
                return inventoryService.findInventoryById(itemId);
            }

            @Override
            protected void succeeded() {
                Optional<Inventory> inventoryOptional = getValue();
                if (inventoryOptional.isPresent()) {
                    Inventory inventory = inventoryOptional.get();
                    itemNameInput.setText(inventory.getItemName());
                    itemTypeComboBox.setValue(inventory.getItemType());
                    availabilityComboBox.setValue(inventory.getAvailability());
                    minSlider.setValue(Double.parseDouble(inventory.getMinStock()));
                    maxSlider.setValue(Double.parseDouble(inventory.getMaxStock()));
                    minStockInput.setText(inventory.getMinStock());
                    stockInput.setText(inventory.getStocks());
                    stockSlider.setValue(Double.parseDouble(inventory.getStocks()));

                    itemLabel.setText(DateFormatter.toTimeStamp(ZonedDateTime.now()) + ".jpg");
                    loadItemImage(Firestore.ITEM.getPath(), inventory.getItemImageUrl());
                }
            }
        };

        new Thread(populateFieldsTask).start();
    }

    private void loadItemImage(String directory, String link) {
        itemImage.setOnMouseClicked(_ -> modalUtils.showImageView(itemImage.getImage(), currentStage));
        itemImage.setCursor(Cursor.HAND);
        itemImage.setVisible(false);
        itemImage.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(itemImageContainer.getWidth(), itemImageContainer.getHeight());
        Platform.runLater(() -> itemImageContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            image = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                itemImageContainer.getChildren().remove(loadingIndicator);
                itemImage.setImage(image);
                itemImage.setVisible(true);
                itemImage.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> itemImageContainer.getChildren().remove(loadingIndicator));
            itemImage.setVisible(true);
            itemImage.setManaged(true);
            System.err.println("Error loading item image");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void validateFields() {
        HBox[] uploadBtns = {uploadItemImage};
        TextField[] textFields = {itemNameInput};
        TextField[] numberFields = {stockInput, minStockInput, maxStockInput};
        ComboBox[] comboBoxes = {itemTypeComboBox, availabilityComboBox};

        if (validator.hasEmptyFields(textFields, (DatePicker[]) null, comboBoxes, numberFields)) return;

        addItem();
    }

    private void showImageView(Image image) {
        modalUtils.showImageView(image, currentStage);
    }

    private void setupEventListeners() {
        closeBtn.setOnMouseClicked(_ -> closeWindowConfirmation());
        cancelBtn.setOnAction(_ -> closeWindowConfirmation());
        confirmBtn.setOnAction(_ -> validateFields());

        uploadItemImage.setOnMouseClicked(_ -> {
            uploadImageUtils.loadSetupFile(currentStage, image -> {
                this.image = image;
                itemImage.setCursor(Cursor.HAND);
                itemImage.setImage(image);
                itemImage.setOnMouseClicked(_ -> showImageView(image));
                itemLabel.setText(DateFormatter.toTimeStamp(ZonedDateTime.now()) + ".jpg");
                uploadItemImage.setStyle(null);
            });
        });

        availabilityComboBox.getItems().addAll(Arrays.stream(ItemAvailability.values()).map(item -> item.getName()).toList());
        itemTypeComboBox.getItems().addAll(Arrays.stream(Itemtype.values()).map(item -> item.getName()).toList());
        validator.setupComboBox(List.of(availabilityComboBox, itemTypeComboBox));
        validator.createNumberFormatter(new TextField[]{stockInput, minStockInput, maxStockInput});

        stockInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    double value = Double.parseDouble(newValue);
                    double maxStock = Double.parseDouble(maxStockInput.getText());
                    double minStock = Double.parseDouble(minStockInput.getText());
                    if (value > maxStock || value < minStock) {
                        stockInput.setText(oldValue);
                    } else {
                        stockSlider.setValue(value);
                    }
                } catch (NumberFormatException e) {
                    stockInput.setText(oldValue);
                }
            }
        });

        minStockInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    double value = Double.parseDouble(newValue);
                    double maxStock = maxStockInput.getText().isEmpty() ? 0 : Double.parseDouble(maxStockInput.getText());
                    if (value > maxStock) {
                        minStockInput.setText(oldValue);
                    } else {
                        minSlider.setValue(value);
                    }
                } catch (NumberFormatException e) {
                    minStockInput.setText(oldValue);
                }
            }
        });

        maxStockInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    double value = Double.parseDouble(newValue);
                    double minStock = minStockInput.getText().isEmpty() ? 0 : Double.parseDouble(minStockInput.getText());
                    if (value < minStock) {
                        maxStockInput.setText(oldValue);
                    } else {
                        maxSlider.setValue(value);
                    }
                } catch (NumberFormatException e) {
                    maxStockInput.setText(oldValue);
                }
            }
        });

        minSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            minStockInput.setText(String.valueOf(newValue.intValue()));
            adjustStockSlider();
        });

        maxSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            maxStockInput.setText(String.valueOf(newValue.intValue()));
            adjustStockSlider();
        });

        maxSlider.setMax(100000);
        minSlider.setMax(100000);
        maxSlider.setBlockIncrement(1);
        maxSlider.setMajorTickUnit(1);
        minSlider.setBlockIncrement(1);
        minSlider.setMajorTickUnit(1);

        stockSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            stockInput.setText(String.valueOf(newValue.intValue()));
        });
    }

    private void adjustStockSlider() {
        try {
            double minStock = Double.parseDouble(minStockInput.getText());
            double maxStock = Double.parseDouble(maxStockInput.getText());
            stockSlider.setMin(minStock);
            stockSlider.setMax(maxStock);
            stockSlider.setBlockIncrement(1);
            stockSlider.setMajorTickUnit(1);
        } catch (NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }

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
        this.itemId = id;
    }
}