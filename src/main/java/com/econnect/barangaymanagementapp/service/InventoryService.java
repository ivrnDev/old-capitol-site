package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
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
        inventory.setCreatedAt(ZonedDateTime.now());
        inventory.setUpdatedAt(ZonedDateTime.now());
        return inventoryRepository.createInventory(inventory);
    }

    public Response updateInventory(Inventory inventory) {
        inventory.setUpdatedAt(ZonedDateTime.now());
        return inventoryRepository.updateInventory(inventory);
    }

    public Response updateStatus(String id, StatusType.InventoryStatus status) {
        return inventoryRepository.updateInventoryByStatus(id, status);
    }

    public List<Inventory> findAllInventories() {
        return inventoryRepository.findAllInventory();
    }

    public List<Inventory> findAllPublicInventories() {
        return inventoryRepository.findInventoryByFilter(inventory -> inventory.getAvailability().equals("Public"));
    }

    public Optional<Inventory> findInventoryById(String id) {
        return inventoryRepository.findInventoryById(id);
    }

    public int generateId() {
        int baseId = 1000;
        int countOfInventoriess = findCountOfInventory();
        return countOfInventoriess > 0 ? baseId + countOfInventoriess : baseId;
    }

    private int findCountOfInventory() {
        return (int) findAllInventories().stream().count();
    }


    public void enableLiveReload(Consumer<String> handleDataUpdate) {
        inventoryRepository.enableLiveReload(handleDataUpdate);
    }
}
