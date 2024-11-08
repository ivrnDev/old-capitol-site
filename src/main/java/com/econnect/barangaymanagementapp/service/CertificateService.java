package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.repository.CertificateRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.RequestStatus.PENDING;

public class CertificateService {
    private final CertificateRepository certificateRepository;

    public CertificateService(DependencyInjector dependencyInjector) {
        this.certificateRepository = dependencyInjector.getCertificateRepository();
    }

    public Response createCertificate(Request request) {
        int baseId = 1000;
        String residentId = request.getId();
        int countOfCertificates = findCountOfCertificatesByResidentId(residentId);
        int autoIncrementId = countOfCertificates > 0 ? baseId + countOfCertificates : baseId;
        request.setId(request.getId() + "-" + autoIncrementId);
        request.setReferenceNumber(generateReferenceNumber());
        request.setCreatedAt(ZonedDateTime.now());
        request.setUpdatedAt(ZonedDateTime.now());
        request.setApplicationType(ApplicationType.WALK_IN);
        request.setStatus(PENDING);
        return certificateRepository.createCertificate(request);
    }

    public List<Request> findAllCertificates() {
        return certificateRepository.findAllCertificates();
    }

    public List<Request> findAllPendingCertificates() {
        return certificateRepository.findCertificateByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<Request> findCertificateById(String id) {
        return certificateRepository.findCertificateById(id);
    }

    private int findCountOfCertificatesByResidentId(String residentId) {
        return (int) certificateRepository.findCertificateByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public Optional<Request> findCompletedCertificate(String id) {
        return certificateRepository.findCertificateById(id).filter(request -> request.getStatus().equals(StatusType.RequestStatus.COMPLETED));
    }

    public Response updateCertificateByStatus(String requestId, StatusType.RequestStatus status) {
        return certificateRepository.updateCertificateByStatus(requestId, status);
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }
}
