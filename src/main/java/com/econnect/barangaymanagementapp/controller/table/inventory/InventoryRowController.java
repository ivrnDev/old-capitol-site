package com.econnect.barangaymanagementapp.controller.table.inventory;


import com.econnect.barangaymanagementapp.controller.base.BaseRowController;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.service.InventoryService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.StockLevel;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;

import static com.econnect.barangaymanagementapp.util.StockLevel.OUT_OF_STOCK;

public class InventoryRowController extends BaseRowController<Inventory> {
    @FXML
    private HBox tableRow, buttonContainer, stockLevelContainer;
    @FXML
    private Label itemIdLabel, itemNameLabel, itemTypeLabel, stockLevel, itemStocksLabel;
    @FXML
    private ImageView itemPicture;

    private final ModalUtils modalUtils;
    private final Stage parentStage;
    private InventoryService inventoryService;
    private final DependencyInjector dependencyInjector;
    private final UserSession userSession;
    @Getter
    private String itemId;

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
        Platform.runLater(() -> setupButtonContainer());
        this.itemId = itemData.getId();

        itemIdLabel.setText(itemData.getId());
        itemNameLabel.setText(itemData.getItemName());
        itemTypeLabel.setText(itemData.getItemType());

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
        switch (StockLevel.fromName(stockLevel.getText())) {
            case OUT_OF_STOCK -> setStockLevelColor("#FF3B30");
            case CRITICAL -> setStockLevelColor("#FF9500");
            case SAFETY_STOCK -> setStockLevelColor("#FFCC00");
            case LOW -> setStockLevelColor("#007AFF");
            case MEDIUM -> setStockLevelColor("#34C759");
            case MEDIUM_HIGH -> setStockLevelColor("#32CD32");
            case HIGH -> setStockLevelColor("#0EAA1D");
        }
    }

    private void setStockLevelColor(String color) {
        stockLevelContainer.setStyle("-fx-background-color: " + color);
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