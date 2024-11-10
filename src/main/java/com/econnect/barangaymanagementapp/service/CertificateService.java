package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.CertificateStatus;
import com.econnect.barangaymanagementapp.repository.CertificateRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.CertificateStatus.PENDING;

public class CertificateService {
    private final CertificateRepository certificateRepository;

    public CertificateService(DependencyInjector dependencyInjector) {
        this.certificateRepository = dependencyInjector.getCertificateRepository();
    }

    public Response createCertificate(Certificate certificate) {
        int baseId = 1000;
        String residentId = certificate.getId();
        int countOfCertificates = findCountOfCertificatesByResidentId(residentId);
        int autoIncrementId = countOfCertificates > 0 ? baseId + countOfCertificates : baseId;
        certificate.setId(certificate.getId() + "-" + autoIncrementId);
        certificate.setReferenceNumber(generateReferenceNumber());
        certificate.setCreatedAt(ZonedDateTime.now());
        certificate.setUpdatedAt(ZonedDateTime.now());
        certificate.setApplicationType(ApplicationType.WALK_IN);
        certificate.setStatus(CertificateStatus.PENDING);
        return certificateRepository.createCertificate(certificate);
    }

    public List<Certificate> findAllCertificates() {
        return certificateRepository.findAllCertificates();
    }

    public List<Certificate> findAllPendingCertificates() {
        return certificateRepository.findCertificateByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<Certificate> findCertificateById(String id) {
        return certificateRepository.findCertificateById(id);
    }

    private int findCountOfCertificatesByResidentId(String residentId) {
        return (int) certificateRepository.findCertificateByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public Optional<Certificate> findCompletedCertificate(String id) {
        return certificateRepository.findCertificateById(id).filter(request -> request.getStatus().equals(StatusType.CertificateStatus.COMPLETED));
    }

    public Response updateCertificateByStatus(String requestId, StatusType.CertificateStatus status) {
        return certificateRepository.updateCertificateByStatus(requestId, status);
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        certificateRepository.enableLiveReload(handleDataUpdate);
    }
}
