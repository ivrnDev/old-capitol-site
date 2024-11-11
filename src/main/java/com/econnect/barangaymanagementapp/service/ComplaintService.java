package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Complaint;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.ComplaintStatus;
import com.econnect.barangaymanagementapp.repository.ComplaintRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ComplaintStatus.PENDING;

public class ComplaintService {
    private final ComplaintRepository complaintRepository;

    public ComplaintService(DependencyInjector dependencyInjector) {
        this.complaintRepository = dependencyInjector.getComplaintRepository();
    }

    public Response createComplaint(Complaint complaint) {
        int baseId = 1000;
        String residentId = complaint.getId();
        int countOfComplaints = findCountOfComplaintsByResidentId(residentId);
        int autoIncrementId = countOfComplaints > 0 ? baseId + countOfComplaints : baseId;
        complaint.setId(complaint.getId() + "-" + autoIncrementId);
        complaint.setCaseNumber(generateReferenceNumber());
        complaint.setCreatedAt(ZonedDateTime.now());
        complaint.setUpdatedAt(ZonedDateTime.now());
        complaint.setApplicationType(ApplicationType.WALK_IN);
        complaint.setStatus(ComplaintStatus.PENDING);
        return complaintRepository.createComplaint(complaint);
    }

    public List<Complaint> findAllComplaints() {
        return complaintRepository.findAllComplaints();
    }

    public List<Complaint> findAllPendingComplaints() {
        return complaintRepository.findComplaintByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<Complaint> findComplaintById(String id) {
        return complaintRepository.findComplaintById(id);
    }

    private int findCountOfComplaintsByResidentId(String residentId) {
        return (int) complaintRepository.findComplaintByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public Optional<Complaint> findCompletedComplaint(String id) {
        return complaintRepository.findComplaintById(id).filter(request -> request.getStatus().equals(ComplaintStatus.RESOLVED));
    }

    public Response updateComplaintByStatus(String requestId, ComplaintStatus status) {
        return complaintRepository.updateComplaintByStatus(requestId, status);
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        complaintRepository.enableLiveReload(handleDataUpdate);
    }
}
