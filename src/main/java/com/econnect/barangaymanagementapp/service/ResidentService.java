package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Account;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.repository.AccountRepository;
import com.econnect.barangaymanagementapp.repository.ResidentRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus.*;

public class ResidentService {
    private final ResidentRepository residentRepository;
    private final AccountRepository accountRepository;
    private final Set<StatusType.ResidentStatus> INACTIVE_RESIDENT = Set.of(DECEASED, MIGRATED, SUSPENDED);

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

    public List<Resident> findAllNonDeletedAndPendingResidents() {
        return residentRepository.findResidentByFilter(resident -> !resident.getStatus().equals(REMOVED) && !resident.getStatus().equals(PENDING));
    }

    public List<Resident> findAllPendingResidents() {
        return residentRepository.findResidentByFilter(resident -> resident.getStatus().equals(PENDING));
    }

    public Optional<Resident> findResidentById(String id) {
        return residentRepository.findResidentById(id);
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

    public String generateResidentId() {
        int OTP_LENGTH = 6;
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt((int) Math.pow(10, OTP_LENGTH));
        return String.format("%06d", otp);
    }
}
