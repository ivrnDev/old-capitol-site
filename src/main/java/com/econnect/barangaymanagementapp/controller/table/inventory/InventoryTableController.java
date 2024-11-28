package com.econnect.barangaymanagementapp.controller.table.inventory;

import com.econnect.barangaymanagementapp.controller.base.BaseTableController;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.INVENTORY_ROW;

public class InventoryTableController extends BaseTableController<Inventory> {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;

    public InventoryTableController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
    }

    @Override
    public void addRow(Inventory inventoryData) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(INVENTORY_ROW.getFxmlPath(), dependencyInjector);
            HBox inventoryRow = loader.load();
            InventoryRowController inventoryRowController = loader.getController();
            Image defaultImage = super.getImageOrDefault(inventoryData.getId());
            inventoryRow.setUserData(inventoryRowController);
            inventoryRowController.setImage(defaultImage);
            inventoryRowController.setData(inventoryData);
            super.loadImage(inventoryData.getId(), inventoryData.getItemImageUrl(), inventoryRowController, false);
            tableContent.getChildren().add(inventoryRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding inventory row: " + e.getMessage(), e);
        }
    }

    public void updateRow(Inventory updatedInventory) {
        boolean rowExist = false;
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox inventoryRow) {
                InventoryRowController rowController = (InventoryRowController) inventoryRow.getUserData();
                if (rowController != null && rowController.getItemId().equals(updatedInventory.getId())) {
                    rowController.setData(updatedInventory);
                    super.loadImage(updatedInventory.getId(), updatedInventory.getItemImageUrl(), rowController, true);
                    rowExist = true;
                    break;
                }
            }
        }

        if (!rowExist) {
            super.removeNoDataRow();
            addRow(updatedInventory);
        }
    }

    public void deleteRow(String employeeId) {
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox inventoryRow) {
                InventoryRowController rowController = (InventoryRowController) inventoryRow.getUserData();
                if (rowController != null && rowController.getItemId().equals(employeeId)) {
                    tableContent.getChildren().remove(inventoryRow);
                    break;
                }
            }
        }

        if (tableContent.getChildren().isEmpty()) {
            super.showNoData();
        }
    }
}
