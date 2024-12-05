package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.BarangayId;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.repository.BarangayIdRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.BarangayIdStatus;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.BarangayIdStatus.PENDING;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.BarangayIdStatus.RELEASING;

public class BarangayidService {
    private final BarangayIdRepository barangayIdRepository;
    private final EmailService emailService;
    private final ResidentService residentService;

    public BarangayidService(DependencyInjector dependencyInjector) {
        this.barangayIdRepository = dependencyInjector.getBarangayIdRepository();
        this.emailService = dependencyInjector.getEmailService();
        this.residentService = dependencyInjector.getResidentService();
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
        BarangayId barangayId = findBarangayIdById(requestId).get();

        if (status.equals(RELEASING) && barangayId.getApplicationType().equals(ApplicationType.ONLINE)) {
            Resident resident = residentService.findResidentById(barangayId.getId().substring(0, 12)).orElseThrow();
            sendReleaseEmail(resident, barangayId);
        }
        return barangayIdRepository.updateBarangayIdByStatus(requestId, status);
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }

    // Analytics
    public int totalBarangayId() {
        return findAllBarangayIds().size();
    }

    public int todayTotalBarangayIdRequests() {
        return barangayIdRepository.findBarangayIdByFilter(request -> request.getCreatedAt().toLocalDate().equals(ZonedDateTime.now().toLocalDate())).size();
    }

    public int totalPendingBarangayIdRequests() {
        return findAllPendingBarangayIds().size();
    }

    public int totalProcessingBarangayIdRequests() {
        return barangayIdRepository.findBarangayIdByFilter(request -> request.getStatus().equals(BarangayIdStatus.RELEASING)).size();
    }

    public int totalCompletdBarangayId() {
        return barangayIdRepository.findBarangayIdByFilter(request -> request.getStatus().equals(BarangayIdStatus.COMPLETED)).size();
    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        barangayIdRepository.enableLiveReload(handleDataUpdate);
    }

    private void sendReleaseEmail(Resident resident, BarangayId barangayId) {
        emailService.sendEmailAsync(resident.getEmail(), "Your Barangay ID is Ready for Pickup", String.format("""
                        Dear %s,

                        We are pleased to inform you that your barangay id with reference number %s has been processed and is now ready for pickup.
                                                    
                        Please visit the barangay hall at Old Capitol Site to collect your barangayId. Our office hours are from 8:00 AM to 5:00 PM, Monday to Friday.
                                                    
                        If you have any questions or need further assistance, feel free to reach out to our office.
                                                        
                        Best regards,
                        Old Capitol Site
                        """,
                resident.getFirstName(),
                barangayId.getReferenceNumber()
        ));
    }
}
