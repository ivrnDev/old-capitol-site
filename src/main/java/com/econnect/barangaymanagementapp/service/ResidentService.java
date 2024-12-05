package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.repository.AccountRepository;
import com.econnect.barangaymanagementapp.repository.ResidentRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus.PENDING;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus.VERIFIED;
import static com.econnect.barangaymanagementapp.util.StatusUtils.INACTIVE_RESIDENT;

public class ResidentService {
    private final ResidentRepository residentRepository;
    private final AccountRepository accountRepository;
    private final EmployeeService employeeService;
    private final EmailService emailService;

    public ResidentService(DependencyInjector dependencyInjector) {
        this.residentRepository = dependencyInjector.getResidentRepository();
        this.accountRepository = dependencyInjector.getAccountRepository();
        this.emailService = dependencyInjector.getEmailService();
        this.employeeService = dependencyInjector.getEmployeeService();
    }

    public Response createResident(Resident resident) {
        return residentRepository.createResident(resident);
    }

    public List<Resident> findAllResidents() {
        return residentRepository.findAllResidents();
    }

    public List<Resident> findAllActiveResidents() {
        return residentRepository.findResidentByFilter(resident -> !INACTIVE_RESIDENT.contains(resident.getStatus()));
    }

    public List<Resident> findAllPendingResidents() {
        return residentRepository.findResidentByFilter(resident -> resident.getStatus().equals(PENDING));
    }

    public List<Resident> findAllRejectedResidents() {
        return residentRepository.findResidentByFilter(resident -> resident.getStatus().equals(StatusType.ResidentStatus.REJECTED));
    }

    public Optional<Resident> findResidentById(String id) {
        return residentRepository.findResidentById(id);
    }

    public Optional<Resident> findVerifiedResidentById(String id) {
        return residentRepository.findResidentById(id).filter(resident -> resident.getStatus().equals(VERIFIED));
    }

    public Response updateResidentByStatus(String residentId, StatusType.ResidentStatus status) {
        try {
            Response updateAccountStatus = accountRepository.updateAccountByStatus(residentId, status);
            Response updateResidentStatus = residentRepository.updateResidentByStatus(residentId, status);
            Resident resident = residentRepository.findResidentById(residentId).orElseThrow();
            if (updateAccountStatus != null && updateAccountStatus.isSuccessful() && status.equals(VERIFIED)) {
                emailService.sendEmailAsync(resident.getEmail(), "Congratulations! You are now verified", String.format("""
                                Dear %s,

                                We are delighted to welcome you as an official resident of Old Capitol Site! Your application has been reviewed and verified, and we are excited to have you join our community.

                                As a resident, you are now entitled to various services and privileges offered within our locality. We look forward to your active participation in building a vibrant and supportive community.

                                If you have any questions or need assistance, please don't hesitate to reach out to us at our office or through email.

                                You may now visit and login in our website at https://old-capitol-site-69.netlify.app/ to access various services.

                                Your Resident ID is: %s

                                Best regards,
                                Old Capitol Site Administration
                                """,
                        resident.getFirstName(),
                        resident.getId()
                ));
            }

            if (updateAccountStatus == null && status.equals(VERIFIED)) {
                emailService.sendEmailAsync(resident.getEmail(), "Congratulations! You are now verified", String.format("""
                                Dear %s,

                                We are delighted to welcome you as an official resident of Old Capitol Site! Your application has been reviewed and verified, and we are excited to have you join our community.

                                As a resident, you are now entitled to various services and privileges offered within our locality. We look forward to your active participation in building a vibrant and supportive community.

                                If you have any questions or need assistance, please don't hesitate to reach out to us at our office or through email.

                                To access the services online you may visit our website at https://old-capitol-site-69.netlify.app/ and signup using your Resident ID.
                                                              
                                Your Resident ID is: %s
                                                                
                                Best regards,
                                Old Capitol Site Administration
                                """,
                        resident.getFirstName(),
                        resident.getId()
                ));
            }

            return updateResidentStatus;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response updateResidentAsync(Resident resident) {
        Optional<Employee> employee = employeeService.findEmployeeById(resident.getId());
        employee.ifPresentOrElse(
                value -> {
                    value.setFirstName(resident.getFirstName());
                    value.setLastName(resident.getLastName());
                    value.setMiddleName(resident.getMiddleName());
                    value.setEmail(resident.getEmail());
                    value.setAddress(resident.getAddress());
                    value.setContactNumber(resident.getMobileNumber());
                    value.setEmail(resident.getEmail());
                    value.setProfileUrl(resident.getProfileUrl());
                    employeeService.updateEmployee(value);
                },
                () -> {
                    throw new RuntimeException("Employee not found");
                }
        );
        return residentRepository.updateResident(resident);
    }

    public Response updateResidentTin(String residentId, String tinNumber, String tinUrl) {
        return residentRepository.updateResidentTin(residentId, tinNumber, tinUrl);
    }

    public String generateResidentId() {
        int OTP_LENGTH = 5;
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt((int) Math.pow(10, OTP_LENGTH));
        return String.format("00%05d-%d", otp, Year.now().getValue());
    }

    //Analytics
    public int getPendingResident() {
        return residentRepository.findResidentByFilter(resident -> resident.getStatus().equals(PENDING)).size();
    }

    public int getTodayApplicants() {
        return residentRepository.findResidentByFilter(resident ->
                resident.getStatus().equals(PENDING) && resident.getCreatedAt().toLocalDate().equals(LocalDate.now())).size();
    }

    public int getSuspendedResident() {
        return residentRepository.findResidentByFilter(resident -> resident.getStatus().equals(StatusType.ResidentStatus.SUSPENDED)).size();
    }

    public int getRejectedResident() {
        return residentRepository.findResidentByFilter(resident -> resident.getStatus().equals(StatusType.ResidentStatus.REJECTED)).size();
    }

    public int getVerifiedResident() {
        return residentRepository.findResidentByFilter(resident -> resident.getStatus().equals(VERIFIED)).size();
    }


    //Update Listener
    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        residentRepository.enableLiveReload(handleDataUpdate);
    }
}
