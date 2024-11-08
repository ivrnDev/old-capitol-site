package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CertificateRepository extends BaseRepository<Request> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.CERTIFICATES.getPath();

    public CertificateRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createCertificate(Request request) {
        return create(apiKey + "/" + request.getId(), request);
    }

    public Response updateCertificate(Request request) {
        return create(apiKey + "/" + request.getId(), request);

    }

    public Boolean deleteCertificateById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<Request> findCertificateById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<Request> findAllCertificates() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<Request> findCertificateByFilter(Predicate<Request> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public Response updateCertificateByStatus(String requestId, StatusType.RequestStatus status) {
        return updateBy(apiKey, requestId, new TypeReference<>() {
        }, request -> request.setStatus(status));
    }

}
