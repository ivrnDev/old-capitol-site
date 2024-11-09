package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CertificateRepository extends BaseRepository<Certificate> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.CERTIFICATES.getPath();

    public CertificateRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createCertificate(Certificate certificate) {
        return create(apiKey + "/" + certificate.getId(), certificate);
    }

    public Response updateCertificate(Certificate certificate) {
        return create(apiKey + "/" + certificate.getId(), certificate);

    }

    public Boolean deleteCertificateById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<Certificate> findCertificateById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<Certificate> findAllCertificates() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<Certificate> findCertificateByFilter(Predicate<Certificate> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public Response updateCertificateByStatus(String requestId, StatusType.CertificateStatus status) {
        return updateBy(apiKey, requestId, new TypeReference<>() {
        }, request -> request.setStatus(status));
    }

}
