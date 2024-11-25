package com.econnect.barangaymanagementapp.controller;

import com.econnect.barangaymanagementapp.controller.table.inventory.InventoryTableController;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.service.InventoryService;
import com.econnect.barangaymanagementapp.service.SearchService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class InventoryController {

    @FXML
    private VBox contentPane;
    @FXML
    private TextField searchField;
    @FXML
    private Button addItemButton;

    private final InventoryService inventoryService;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private InventoryTableController inventoryTableController;
    private final SearchService<Inventory> searchService;
    private final LiveReloadUtils liveReloadUtils;
    private final DependencyInjector dependencyInjector;
    private List<Inventory> allInventory;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public InventoryController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.inventoryService = dependencyInjector.getInventoryService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.searchService = dependencyInjector.getInventorySearchService();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
    }

    public void initialize() {
        resetLiveReload();
        initializeSSEListener();
        loadInventoryTable();
        populateInventoryRows();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performSearch());
            searchDelay.playFromStart();
        });
        addItemButton.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.ADD_ITEM));
    }

    private void loadInventoryTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(FXMLPath.INVENTORY_TABLE.getFxmlPath(), dependencyInjector);
            Parent inventoryTable = loader.load();
            inventoryTableController = loader.getController();
            contentPane.getChildren().add(inventoryTable);
        } catch (IOException e) {
            System.err.println("Error loading inventory table: " + e.getMessage());
        }
    }

    public void populateInventoryRows() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(contentPane.getWidth(), contentPane.getHeight());
        Platform.runLater(() -> contentPane.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            allInventory = inventoryService.findAllInventories();
            Platform.runLater(() -> {
                contentPane.getChildren().remove(loadingIndicator);
                if (allInventory.isEmpty()) {
                    inventoryTableController.clearRow();
                    inventoryTableController.showNoData();
                } else {
                    updateInventoryTable(allInventory);
                }
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> contentPane.getChildren().remove(loadingIndicator));
            System.err.println("Error loading inventories");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        searchService.performSearch(
                searchText,
                allInventory,
                searchService.createInventoryFilter(searchText),
                (filteredInventory) -> updateInventoryTable(filteredInventory));
    }

    private void updateInventoryTable(List<Inventory> inventories) {
        inventoryTableController.clearRow();

        if (inventories.isEmpty()) {
            inventoryTableController.showNoData();
            return;
        }
        inventories.forEach(inventory -> inventoryTableController.addRow(inventory));
    }

    public void updateInventoryRow(String id) {
        Optional<Inventory> updatedInventory = inventoryService.findInventoryById(id);
        updatedInventory.ifPresentOrElse(inventory -> {
            inventoryTableController.updateRow(inventory);
        }, () -> inventoryTableController.deleteRow(id));
    }

    private void initializeSSEListener() {
        inventoryService.enableLiveReload(result -> Platform.runLater(() -> updateInventoryRow(result)));
    }

    private void resetLiveReload() {
        liveReloadUtils.stopListeningToUpdates();
    }
}