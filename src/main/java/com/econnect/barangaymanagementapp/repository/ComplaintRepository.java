package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Complaint;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ComplaintRepository extends BaseRepository<Complaint> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.COMPLAINT.getPath();

    public ComplaintRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createComplaint(Complaint cedula) {
        return create(apiKey + "/" + cedula.getId(), cedula);
    }

    public Response updateComplaint(Complaint cedula) {
        return create(apiKey + "/" + cedula.getId(), cedula);

    }

    public Boolean deleteComplaintById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<Complaint> findComplaintById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<Complaint> findAllComplaints() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<Complaint> findComplaintByFilter(Predicate<Complaint> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public Response updateComplaintByStatus(String requestId, StatusType.ComplaintStatus status) {
        return updateBy(apiKey, requestId, new TypeReference<>() {
        }, request -> request.setStatus(status));
    }

    public void enableLiveReload(Consumer<String> handleDataUpdates) {
        enableLiveReload(apiKey, handleDataUpdates, "COMPLAINT:");
    }

}
