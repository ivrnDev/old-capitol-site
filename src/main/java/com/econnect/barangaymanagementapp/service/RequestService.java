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

    private int findCountOfRequestsByResidentId(String residentId) {
        return (int) requestRepository.findRequestByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public Optional<Request> findCompletedRequest(String id) {
        return requestRepository.findRequestById(id).filter(request -> request.getStatus().equals(RequestStatus.COMPLETED));
    }

    public Response updateRequestByStatus(String requestId, RequestStatus status) {
        return requestRepository.updateRequestByStatus(requestId, status);
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }
}
