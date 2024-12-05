package com.econnect.barangaymanagementapp.controller.table.inventory;


import com.econnect.barangaymanagementapp.controller.base.BaseRowController;
import com.econnect.barangaymanagementapp.controller.form.EditItemController;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.service.InventoryService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.StockLevel;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.ButtonUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;
import okhttp3.Response;

import static com.econnect.barangaymanagementapp.util.StockLevel.OUT_OF_STOCK;

public class InventoryRowController extends BaseRowController<Inventory> {
    @FXML
    private HBox tableRow, buttonContainer, stockLevelContainer;
    @FXML
    private Label itemIdLabel, itemNameLabel, itemTypeLabel, stockLevel, itemStocksLabel, statusLabel;
    @FXML
    private ImageView itemPicture;

    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private InventoryService inventoryService;
    private final DependencyInjector dependencyInjector;
    private final UserSession userSession;
    @Getter
    private String itemId;
    private Inventory inventory;

    public InventoryRowController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.dependencyInjector = dependencyInjector;
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
        this.inventoryService = dependencyInjector.getInventoryService();
        this.userSession = UserSession.getInstance();
    }

    public void initialize() {
        setupProfileImageClick();
        setupRowClickEvents();
    }

    @Override
    protected void setData(Inventory itemData) {
        this.itemId = itemData.getId();
        this.inventory = itemData;
        Platform.runLater(() -> setupButtonContainer());

        itemIdLabel.setText(itemData.getId());
        itemNameLabel.setText(itemData.getItemName());
        itemTypeLabel.setText(itemData.getItemType());
        statusLabel.setText(itemData.getStatus());
        int currentStock = itemData.getStocks() != null ? Integer.parseInt(itemData.getStocks()) : 0;
        int minStock = itemData.getMinStock() != null ? Integer.parseInt(itemData.getMinStock()) : 0;
        int maxStock = itemData.getMaxStock() != null ? Integer.parseInt(itemData.getMaxStock()) : 0;
        stockLevel.setText(StockLevel.getLevel(currentStock, minStock, maxStock).getName());
        itemStocksLabel.setText(itemData.getStocks());
    }

    @Override
    public void setImage(Image profileImage) {
        itemPicture.setImage(profileImage);
    }

    private void setupProfileImageClick() {
        ImageUtils.setCircleClip(itemPicture);
        itemPicture.setOnMouseClicked(_ -> modalUtils.showImageView(itemPicture.getImage(), parentStage));
    }

    private void setupRowClickEvents() {
        // Add Selection Style
        tableRow.setOnMouseClicked(_ -> {
            if (tableRow.getStyleClass().contains("selected")) {
                tableRow.getStyleClass().remove("selected");
            } else {
                tableRow.getStyleClass().add("selected");
            }
        });
        // Remove selection style
        tableRow.setOnMouseExited(_ -> {
            if (!tableRow.getStyleClass().contains("selected")) {
                tableRow.setStyle("");
            }
        });
    }

    protected void setupButtonContainer() {
        buttonContainer.getChildren().clear();
        switch (StockLevel.fromName(stockLevel.getText())) {
            case OUT_OF_STOCK -> setStockLevelColor("#FF3B30");
            case CRITICAL -> setStockLevelColor("#FF9500");
            case SAFETY_STOCK -> setStockLevelColor("#FFCC00");
            case LOW -> setStockLevelColor("#007AFF");
            case MEDIUM -> setStockLevelColor("#34C759");
            case MEDIUM_HIGH -> setStockLevelColor("#32CD32");
            case HIGH -> setStockLevelColor("#0EAA1D");
        }

        switch (StatusType.InventoryStatus.fromName(inventory.getStatus())) {
            case AVAILABLE -> {
                createEditButton();
                createDeactivateButton();
                createDeleteButton();
            }
            case UNAVAILABLE -> {
                createEditButton();
                creatRestoreButton();
                createDeleteButton();
            }
        }
    }

    private void setStockLevelColor(String color) {
        stockLevelContainer.setStyle("-fx-background-color: " + color);
    }

    private void createEditButton() {
        Button editBtn = ButtonUtils.createButton("Edit", ButtonStyle.UPDATE, () -> {
            modalUtils.customizeModalWithCallback(
                    FXMLPath.EDIT_ITEM,
                    EditItemController.class,
                    controller -> controller.setId(itemId)
            );
        });
        buttonContainer.getChildren().add(editBtn);
    }

    private void createDeactivateButton() {
        Button unavailableBtn = ButtonUtils.createButton("Disable", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT, "Deactivate", "Are you sure you want to make this item unavailable?", isConfirmed -> {
                if (isConfirmed) updateInventoryStatus(StatusType.InventoryStatus.UNAVAILABLE);
            });
        });
        buttonContainer.getChildren().add(unavailableBtn);
    }

    private void creatRestoreButton() {
        Button restoreBtn = ButtonUtils.createButton("Restore", ButtonStyle.ACCEPT, () -> {
            modalUtils.showModal(Modal.DEFAULT, "Restore", "Are you sure you want to restore this item?", isConfirmed -> {
                if (isConfirmed) updateInventoryStatus(StatusType.InventoryStatus.AVAILABLE);
            });
        });
        buttonContainer.getChildren().add(restoreBtn);
    }

    private void createDeleteButton() {
        Button deleteBtn = ButtonUtils.createButton("Delete", ButtonStyle.REJECT, () -> {
            modalUtils.showModal(Modal.DEFAULT, "Delete", "Are you sure you want to delete this item?", isConfirmed -> {
                if (isConfirmed) updateInventoryStatus(StatusType.InventoryStatus.DELETED);
            });
        });
        buttonContainer.getChildren().add(deleteBtn);
    }

    private void updateInventoryStatus(StatusType.InventoryStatus status) {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(tableRow.getWidth(), tableRow.getHeight());

        tableRow.getChildren().forEach(node -> {
            node.setVisible(false);
            node.setManaged(false);
        });
        tableRow.getChildren().add(loadingIndicator);

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() {
                try {
                    return inventoryService.updateStatus(itemId, status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                tableRow.getChildren().remove(loadingIndicator);
                tableRow.getChildren().forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });

                Response response = getValue();
                if (response != null && response.isSuccessful()) {
                    modalUtils.showModal(Modal.SUCCESS, "Success", "Status has been updated successfully.");
                } else {
                    modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while updating the status.");
                }
            }

            @Override
            protected void failed() {
                tableRow.getChildren().remove(loadingIndicator);
                tableRow.getChildren().forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });
                Throwable exception = getException();
                exception.printStackTrace(); // Log the exceptio
                modalUtils.showModal(Modal.ERROR, "Error", exception.getMessage());


            }
        };
        new Thread(task).start();
    }


//    private void setupViewButton() {
//        Button viewBtn = ButtonUtils.createButton("View", ButtonStyle.VIEW, () -> {
//            modalUtils.customizeModalWithCallback(
//                    FXMLPath.VIEW_APPLICATION_EMPLOYEE,
//                    InventoryApplicationController.class,
//                    controller -> controller.setId(employeeIdLabel.getText())
//            );
//        });
//        buttonContainer.getChildren().add(viewBtn);
//    }

}