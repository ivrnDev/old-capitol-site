package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.DepartmentRequest;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DepartmentRequestRepository extends BaseRepository<DepartmentRequest> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.SUPPLY_REQUEST.getPath();

    public DepartmentRequestRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createDepartmentRequest(DepartmentRequest departmentRequest) {
        return create(apiKey + "/" + departmentRequest.getId(), departmentRequest);
    }

    public Response updateDepartmentRequest(DepartmentRequest departmentRequest) {
        return create(apiKey + "/" + departmentRequest.getId(), departmentRequest);

    }

    public Optional<DepartmentRequest> findDepartmentRequestById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });
    }

    public List<DepartmentRequest> findAllDepartmentRequests() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<DepartmentRequest> findDepartmentRequestByFilter(Predicate<DepartmentRequest> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public Response updateDepartmentRequestByStatus(String requestId, String status) {
        return updateBy(apiKey, requestId, new TypeReference<>() {
        }, request -> request.setStatus(status));
    }

    public void enableLiveReload(Consumer<String> handleDataUpdates) {
        enableLiveReload(apiKey, handleDataUpdates, "DepartmentRequest:");
    }

}
