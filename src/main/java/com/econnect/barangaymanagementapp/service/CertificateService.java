package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.*;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.CertificateStatus;
import com.econnect.barangaymanagementapp.repository.CertificateRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.PrintUtils;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import okhttp3.Response;

import java.io.File;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.CertificateStatus.IN_PROGRESS;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.CertificateStatus.PENDING;
import static com.econnect.barangaymanagementapp.util.StatusUtils.TOTAL_PROCESSING_CERTIFICATES;

public class CertificateService {
    private final CertificateRepository certificateRepository;
    private final ResidentService residentService;
    private final EmailService emailService;

    public CertificateService(DependencyInjector dependencyInjector) {
        this.certificateRepository = dependencyInjector.getCertificateRepository();
        this.residentService = dependencyInjector.getResidentService();
        this.emailService = dependencyInjector.getEmailService();
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

    private int countOfCompletedCertificate(CertificateType certificateType) {
        return (int) certificateRepository.findCertificateByFilter(request -> CertificateType.fromName(request.getRequest()).equals(certificateType) && request.getStatus().equals(StatusType.CertificateStatus.COMPLETED)).stream().count();
    }

    private int countOfPrintedCertificate(CertificateType certificateType) {
        return (int) certificateRepository.findCertificateByFilter(request -> CertificateType.fromName(request.getRequest()).equals(certificateType) && request.getStatus().equals(CertificateStatus.IN_PROGRESS)).stream().count();
    }

    public Response updateCertificateByStatus(String requestId, StatusType.CertificateStatus status) {
        var certificate = findCertificateById(requestId);

        if (status.equals(IN_PROGRESS)) addControlNumber(certificate.get());

        if (status.equals(CertificateStatus.RELEASING) && certificate.get().getApplicationType().equals(ApplicationType.ONLINE)) {
            certificate.ifPresent(request -> {
                Optional<Resident> resident = residentService.findResidentById(request.getId().substring(0, 12));
                resident.ifPresent(res -> sendReleaseEmail(res, request));
            });
        }
        return certificateRepository.updateCertificateByStatus(requestId, status);
    }

    public void printCertificate(File pdfFile, Stage currentStage, Consumer<Boolean> callback) {
        try {
            PrintUtils.printDocumentFile(pdfFile, currentStage, callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File generateCertificate(String residentId, Certificate certificate, Consumer<Image> callback) {
        CertificateType certificateType = CertificateType.fromName(certificate.getRequest());
        Optional<Resident> resident = residentService.findResidentById(residentId);
        File[] generatedPdfFile = {null};
        resident.ifPresent(res -> {
            try {
                String controlNumber = generateControlNumber(countOfPrintedCertificate(certificateType), certificateType);
                generatedPdfFile[0] = PrintUtils.generateCertificate(controlNumber, res, certificate, callback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return generatedPdfFile[0];
    }

    public Response addControlNumber(Certificate certificate) {
        CertificateType certificateType = CertificateType.fromName(certificate.getRequest());
        String controlNumber = getControlNumber(certificateType);
        certificate.setControlNumber(controlNumber);
        return certificateRepository.updateCertificate(certificate);
    }

    public String getControlNumber(CertificateType certificateType) {
        int countOfPrintedCertificate = countOfPrintedCertificate(certificateType);
        return generateControlNumber(countOfPrintedCertificate, certificateType);
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }

    private String generateControlNumber(int increment, CertificateType certificateType) {
        String formatCertificate = "";
        switch (certificateType) {
            case CERTIFICATE_OF_INDIGENCY -> formatCertificate = "COI";
            case BARANGAY_CLEARANCE -> formatCertificate = "COC";
            case CERTIFICATE_OF_RESIDENCY -> formatCertificate = "COR";
        }
        int baseId = 1;
        int controlNumber = baseId + increment;
        int currentYear = ZonedDateTime.now().getYear();
        return String.format("%06d-%d-" + formatCertificate, controlNumber, currentYear);
    }

    //Analytics

    public int getTotalCertificates() {
        return certificateRepository.findAllCertificates().size();
    }

    public int todayTotalCertificateRequests() {
        return certificateRepository.findCertificateByFilter(request -> request.getCreatedAt().toLocalDate().equals(LocalDate.now())).size();
    }

    public int totalProcessingCertificates() {
        return certificateRepository.findCertificateByFilter(request -> TOTAL_PROCESSING_CERTIFICATES.contains(request.getStatus())).size();
    }

    public int totalPendingCertificates() {
        return certificateRepository.findCertificateByFilter(request -> request.getStatus().equals(PENDING)).size();
    }

    public int getAllCompletedCertificates() {
        return certificateRepository.findCertificateByFilter(request -> request.getStatus().equals(StatusType.CertificateStatus.COMPLETED)).size();
    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        certificateRepository.enableLiveReload(handleDataUpdate);
    }

    private void sendReleaseEmail(Resident resident, Certificate certificate) {
        emailService.sendEmailAsync(resident.getEmail(), "Your Certificate is Ready for Pickup", String.format("""
                        Dear %s,

                        We are pleased to inform you that your certificate request %s with reference number %s has been processed and is now ready for pickup.
                                                    
                        Please visit the barangay hall at Old Capitol Site to collect your certificate. Our office hours are from 8:00 AM to 5:00 PM, Monday to Friday.
                                                    
                        If you have any questions or need further assistance, feel free to reach out to our office.
                                                        
                        Best regards,
                        Old Capitol Site
                        """,
                resident.getFirstName(),
                certificate.getRequest(),
                certificate.getReferenceNumber()
        ));
    }
}
