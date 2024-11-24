package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.repository.InventoryRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(DependencyInjector dependencyInjector) {
        this.inventoryRepository = dependencyInjector.getInventoryRepository();
    }

    public Response createInventory(Inventory inventory) {
        int baseId = 1000;
        int countOfInventoriess = findCountOfInventory();
        int autoIncrementId = countOfInventoriess > 0 ? baseId + countOfInventoriess : baseId;
        inventory.setId(inventory.getId() + "-" + autoIncrementId);
        inventory.setCreatedAt(ZonedDateTime.now());
        inventory.setUpdatedAt(ZonedDateTime.now());
        return inventoryRepository.createInventory(inventory);
    }

    public List<Inventory> findAllInventories() {
        return inventoryRepository.findAllInventorys();
    }

    public Optional<Inventory> findInventoryById(String id) {
        return inventoryRepository.findInventoryById(id);
    }

    private int findCountOfInventory() {
        return (int) findAllInventories().stream().count();
    }


    public void enableLiveReload(Consumer<String> handleDataUpdate) {
        inventoryRepository.enableLiveReload(handleDataUpdate);
    }
}
