package com.econnect.barangaymanagementapp.controller.detail;

import com.econnect.barangaymanagementapp.controller.base.BaseViewController;
import com.econnect.barangaymanagementapp.domain.Borrow;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.service.BorrowService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.InventoryService;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

public class ViewBorrowController implements BaseViewController {
    @FXML
    private ImageView closeBtn;
    @FXML
    private ImageView profilePicture;
    @FXML
    private HBox profileContainer;
    @FXML
    private TextField residentIdInput, fullNameInput, emailInput, contactNumberInput, quantityInput, returnDateInput, statusInput, dateInput, timeInput;
    @FXML
    private TextArea purposeInput;
    @FXML
    private Text itemIdText, itemNameText, stocksText;

    private final ModalUtils modalUtils;
    private final ResidentService residentService;
    private final BorrowService borrowService;
    private final InventoryService inventoryService;
    private final ImageService imageService;
    private Image profilePictureImage;
    private Stage currentStage;
    private String borrowId;
    private String itemId;

    public ViewBorrowController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.imageService = dependencyInjector.getImageService();
        this.borrowService = dependencyInjector.getBorrowService();
        this.inventoryService = dependencyInjector.getInventoryService();
        Platform.runLater(() -> currentStage = (Stage) closeBtn.getScene().getWindow());
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeView());
        Platform.runLater(this::fetchData);
        setupViewImage();
    }

    private void fetchData() {
        Task<Optional<Borrow>> requestTask = new Task<>() {
            @Override
            protected Optional<Borrow> call() {
                return borrowService.findBorrowById(borrowId);
            }

            @Override
            protected void succeeded() {
                Optional<Borrow> borrowValue = getValue();
                if (borrowValue.isPresent()) {
                    Borrow borrow = borrowValue.get();
                    populateRequestData(borrow);

                    Optional<Inventory> inventoryValue = inventoryService.findInventoryById(itemId);
                    if (inventoryValue.isPresent()) {
                        Inventory inventory = inventoryValue.get();
                        stocksText.setText(String.valueOf(inventory.getStocks()) + "/" + inventory.getMaxStock());
                        loadProfileImage(Firestore.ITEM.getPath(), inventory.getItemImageUrl());
                    }
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        Task<Optional<Resident>> fetchResident = new Task<>() {
            @Override
            protected Optional<Resident> call() {
                return residentService.findResidentById(borrowId.substring(0, borrowId.length() - 5));
            }

            @Override
            protected void succeeded() {
                Optional<Resident> residentValue = getValue();
                if (residentValue.isPresent()) {
                    Resident resident = residentValue.get();
                    populateResidentData(resident);
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        new Thread(fetchResident).start();
        new Thread(requestTask).start();
    }

    private void populateRequestData(Borrow borrow) {
        if (borrow == null) return;
        itemId = borrow.getItemId();
        residentIdInput.setText(borrow.getId().substring(0, borrow.getId().length() - 5));
        itemIdText.setText(borrow.getItemId());
        itemNameText.setText(borrow.getItemName());
        quantityInput.setText(String.valueOf(borrow.getQuantity()));
        returnDateInput.setText(DateFormatter.formatToLongDate(borrow.getReturnedDate()));
        purposeInput.setText(borrow.getPurpose());
        statusInput.setText(borrow.getStatus().getName());
        dateInput.setText(DateFormatter.formatDateToLongStyle(borrow.getCreatedAt()));
        timeInput.setText(DateFormatter.formatTimeTo12HourStyle(borrow.getCreatedAt()));
    }

    private void populateResidentData(Resident resident) {
        if (resident == null) return;
        String fullName = resident.getLastName() + ", " + resident.getFirstName() + " " + resident.getMiddleName();
        fullNameInput.setText(fullName);
        emailInput.setText(resident.getEmail());
        contactNumberInput.setText(resident.getMobileNumber());
    }

    private void loadProfileImage(String directory, String link) {
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), currentStage));
        profilePicture.setCursor(Cursor.HAND);
        profilePicture.setVisible(false);
        profilePicture.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(profileContainer.getWidth(), profileContainer.getHeight());
        Platform.runLater(() -> profileContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            profilePictureImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                profileContainer.getChildren().remove(loadingIndicator);
                profilePicture.setImage(profilePictureImage);
                profilePicture.setVisible(true);
                profilePicture.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> profileContainer.getChildren().remove(loadingIndicator));
            profilePicture.setVisible(true);
            profilePicture.setManaged(true);
            System.err.println("Error loading employees");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void setupViewImage() {
        Platform.runLater(() -> ImageUtils.setRoundedClip(profilePicture, 25, 25));
    }

    @FXML
    private void closeView() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.borrowId = id;
    }
}
