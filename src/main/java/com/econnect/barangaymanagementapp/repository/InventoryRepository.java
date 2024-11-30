package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class InventoryRepository extends BaseRepository<Inventory> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.INVENTORY.getPath();

    public InventoryRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createInventory(Inventory inventory) {
        return create(apiKey + "/" + inventory.getId(), inventory);
    }

    public Response updateInventory(Inventory inventory) {
        return create(apiKey + "/" + inventory.getId(), inventory);

    }

    public Boolean deleteInventoryById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<Inventory> findInventoryById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<Inventory> findAllInventory() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<Inventory> findInventoryByFilter(Predicate<Inventory> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

//    public Response updateInventoryByStatus(String requestId, StatusType.InventoryStatus status) {
//        return updateBy(apiKey, requestId, new TypeReference<>() {
//        }, request -> request.setStatus(status));
//    }

    public void enableLiveReload(Consumer<String> handleDataUpdates) {
        enableLiveReload(apiKey, handleDataUpdates, "INVENTORY:");
    }

}
