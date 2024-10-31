package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.BarangayId;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BarangayIdRepository extends BaseRepository<BarangayId> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.BARANGAYID.getPath();

    public BarangayIdRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createBarangayId(BarangayId request) {
        return create(apiKey + "/" + request.getId(), request);
    }

    public Response updateBarangayId(BarangayId request) {
        return create(apiKey + "/" + request.getId(), request);

    }

    public Boolean deleteBarangayIdById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<BarangayId> findBarangayIdById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<BarangayId> findAllBarangayIds() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<BarangayId> findBarangayIdByFilter(Predicate<BarangayId> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public Response updateBarangayIdByStatus(String requestId, StatusType.BarangayIdStatus status) {
        return updateBy(apiKey, requestId, new TypeReference<>() {
        }, request -> request.setStatus(status));
    }
}
