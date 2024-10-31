package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.BarangayId;
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
    private final BarangayIdRepository requestRepository;

    public BarangayidService(DependencyInjector dependencyInjector) {
        this.requestRepository = dependencyInjector.getBarangayIdRepository();
    }

    public Response createBarangayId(BarangayId request) {
        request.setId(generateBarangayIdId());
        request.setCreatedAt(ZonedDateTime.now());
        request.setUpdatedAt(ZonedDateTime.now());
        request.setStatus(PENDING);
        return requestRepository.createBarangayId(request);
    }

    public List<BarangayId> findAllBarangayIds() {
        return requestRepository.findAllBarangayIds();
    }

    public List<BarangayId> findAllPendingBarangayIds() {
        return requestRepository.findBarangayIdByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<BarangayId> findBarangayIdById(String id) {
        return requestRepository.findBarangayIdById(id);
    }

    public Optional<BarangayId> findCompletedBarangayId(String id) {
        return requestRepository.findBarangayIdById(id).filter(request -> request.getStatus().equals(BarangayIdStatus.COMPLETED));
    }

    public Response updateBarangayIdByStatus(String requestId, BarangayIdStatus status) {
        return requestRepository.updateBarangayIdByStatus(requestId, status);
    }

    private String generateBarangayIdId() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }
}
