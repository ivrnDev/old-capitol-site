package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Cedula;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.CedulaStatus;
import com.econnect.barangaymanagementapp.repository.CedulaRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.CedulaStatus.PENDING;

public class CedulaService {
    private final CedulaRepository certificateRepository;

    public CedulaService(DependencyInjector dependencyInjector) {
        this.certificateRepository = dependencyInjector.getCedulaRepository();
    }

    public Response createCedula(Cedula certificate) {
        int baseId = 1000;
        String residentId = certificate.getId();
        int countOfCedulas = findCountOfCedulasByResidentId(residentId);
        int autoIncrementId = countOfCedulas > 0 ? baseId + countOfCedulas : baseId;
        certificate.setId(certificate.getId() + "-" + autoIncrementId);
        certificate.setReferenceNumber(generateReferenceNumber());
        certificate.setCreatedAt(ZonedDateTime.now());
        certificate.setUpdatedAt(ZonedDateTime.now());
        certificate.setApplicationType(ApplicationType.WALK_IN);
        certificate.setStatus(CedulaStatus.PENDING);
        return certificateRepository.createCedula(certificate);
    }

    public List<Cedula> findAllCedulas() {
        return certificateRepository.findAllCedulas();
    }

    public List<Cedula> findAllPendingCedulas() {
        return certificateRepository.findCedulaByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<Cedula> findCedulaById(String id) {
        return certificateRepository.findCedulaById(id);
    }

    private int findCountOfCedulasByResidentId(String residentId) {
        return (int) certificateRepository.findCedulaByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public Optional<Cedula> findCompletedCedula(String id) {
        return certificateRepository.findCedulaById(id).filter(request -> request.getStatus().equals(CedulaStatus.COMPLETED));
    }

    public Response updateCedulaByStatus(String requestId, CedulaStatus status) {
        return certificateRepository.updateCedulaByStatus(requestId, status);
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
