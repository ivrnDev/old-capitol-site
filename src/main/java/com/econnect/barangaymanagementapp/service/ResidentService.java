package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.repository.AccountRepository;
import com.econnect.barangaymanagementapp.repository.ResidentRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.security.SecureRandom;
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

    public ResidentService(DependencyInjector dependencyInjector) {
        this.residentRepository = dependencyInjector.getResidentRepository();
        this.accountRepository = dependencyInjector.getAccountRepository();
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
            return updateResidentStatus;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response updateResident(Resident resident) {
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

    //Update Listener
    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        residentRepository.enableLiveReload(handleDataUpdate);
    }
}
