package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Borrow;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BorrowRepository extends BaseRepository<Borrow> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.BORROW.getPath();

    public BorrowRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createBorrow(Borrow borrow) {
        return create(apiKey + "/" + borrow.getId(), borrow);
    }

    public Response updateBorrow(Borrow borrow) {
        return create(apiKey + "/" + borrow.getId(), borrow);

    }

    public Boolean deleteBorrowById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<Borrow> findBorrowById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<Borrow> findAllBorrows() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<Borrow> findBorrowByFilter(Predicate<Borrow> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public Response updateBorrowByStatus(String requestId, StatusType.BorrowStatus status) {
        return updateBy(apiKey, requestId, new TypeReference<>() {
        }, request -> request.setStatus(status));
    }

    public void enableLiveReload(Consumer<String> handleDataUpdates) {
        enableLiveReload(apiKey, handleDataUpdates, "BORROW:");
    }

}
