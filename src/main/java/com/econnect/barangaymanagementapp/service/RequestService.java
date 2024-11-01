package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.repository.RequestRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.RequestStatus;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.RequestStatus.PENDING;

public class RequestService {
    private final RequestRepository requestRepository;

    public RequestService(DependencyInjector dependencyInjector) {
        this.requestRepository = dependencyInjector.getRequestRepository();
    }

    public Response createRequest(Request request) {
        request.setId(generateRequestId());
        request.setCreatedAt(ZonedDateTime.now());
        request.setUpdatedAt(ZonedDateTime.now());
        request.setApplicationType(ApplicationType.WALK_IN);
        request.setStatus(PENDING);
        return requestRepository.createRequest(request);
    }

    public List<Request> findAllRequests() {
        return requestRepository.findAllRequests();
    }

    public List<Request> findAllPendingRequests() {
        return requestRepository.findRequestByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<Request> findRequestById(String id) {
        return requestRepository.findRequestById(id);
    }

    public Optional<Request> findCompletedRequest(String id) {
        return requestRepository.findRequestById(id).filter(request -> request.getStatus().equals(RequestStatus.COMPLETED));
    }

    public Response updateRequestByStatus(String requestId, RequestStatus status) {
        return requestRepository.updateRequestByStatus(requestId, status);
    }

    private String generateRequestId() {
        int OTP_LENGTH = 8;
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt((int) Math.pow(10, OTP_LENGTH));
        return String.format("%06d", otp);
    }
}
