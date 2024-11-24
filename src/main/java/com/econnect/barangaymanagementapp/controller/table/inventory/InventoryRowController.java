package com.econnect.barangaymanagementapp.controller.table.inventory;


import com.econnect.barangaymanagementapp.controller.base.BaseRowController;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.service.InventoryService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
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

public class InventoryRowController extends BaseRowController<Inventory> {
    @FXML
    private HBox tableRow, buttonContainer;
    @FXML
    private Label itemIdLabel, itemNameLabel, itemTypeLabel, itemStocksLabel;
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