package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Cedula;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CedulaRepository extends BaseRepository<Cedula> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.CEDULA.getPath();

    public CedulaRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createCedula(Cedula cedula) {
        return create(apiKey + "/" + cedula.getId(), cedula);
    }

    public Response updateCedula(Cedula cedula) {
        return create(apiKey + "/" + cedula.getId(), cedula);

    }

    public Boolean deleteCedulaById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<Cedula> findCedulaById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<Cedula> findAllCedulas() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<Cedula> findCedulaByFilter(Predicate<Cedula> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public Response updateCedulaByStatus(String requestId, StatusType.CedulaStatus status) {
        return updateBy(apiKey, requestId, new TypeReference<>() {
        }, request -> request.setStatus(status));
    }

    public void enableLiveReload(Consumer<String> handleDataUpdates) {
        enableLiveReload(apiKey, handleDataUpdates, "CERTIFICATE:");
    }

}
