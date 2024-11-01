package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.BarangayId;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.repository.BarangayIdRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.BarangayIdStatus;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.BarangayIdStatus.PENDING;

public class BarangayidService {
    private final BarangayIdRepository barangayIdRepository;

    public BarangayidService(DependencyInjector dependencyInjector) {
        this.barangayIdRepository = dependencyInjector.getBarangayIdRepository();
    }

    public Response createBarangayId(BarangayId request) {
        int baseId = 1000;
        String residentId = request.getId();
        int countOfRequests = findCountOfRequestsByResidentId(residentId);
        int autoIncrementId = countOfRequests > 0 ? baseId + countOfRequests : baseId;
        request.setId(request.getId() + "-" + autoIncrementId);
        request.setReferenceNumber(generateReferenceNumber());
        request.setCreatedAt(ZonedDateTime.now());
        request.setUpdatedAt(ZonedDateTime.now());
        request.setApplicationType(ApplicationType.WALK_IN);
        request.setStatus(PENDING);
        return barangayIdRepository.createBarangayId(request);
    }

    private int findCountOfRequestsByResidentId(String residentId) {
        return (int) barangayIdRepository.findBarangayIdByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public List<BarangayId> findAllBarangayIds() {
        return barangayIdRepository.findAllBarangayIds();
    }

    public List<BarangayId> findAllPendingBarangayIds() {
        return barangayIdRepository.findBarangayIdByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<BarangayId> findBarangayIdById(String id) {
        return barangayIdRepository.findBarangayIdById(id);
    }

    public Optional<BarangayId> findCompletedBarangayId(String id) {
        return barangayIdRepository.findBarangayIdById(id).filter(request -> request.getStatus().equals(BarangayIdStatus.COMPLETED));
    }

    public Response updateBarangayIdByStatus(String requestId, BarangayIdStatus status) {
        return barangayIdRepository.updateBarangayIdByStatus(requestId, status);
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }
}
