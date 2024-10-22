package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.repository.ResidentRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus.*;

public class ResidentService {
    private final ResidentRepository residentRepository;
    private final Set<StatusType.ResidentStatus> INACTIVE_RESIDENT = Set.of(DECEASED, MIGRATED, REMOVED, SUSPENDED);

    public ResidentService(DependencyInjector dependencyInjector) {
        this.residentRepository = dependencyInjector.getResidentRepository();
    }

    public Response createResident(Resident resident) {
        return residentRepository.createResident(resident);
    }

    public List<Resident> findAllResidents() {
        return residentRepository.findAllResidents();
    }

    public List<Resident> findAllPendingResidents() {
        return residentRepository.findResidentByFilter(resident -> resident.getStatus().equals(PENDING));
    }

    public List<Resident> findAllNonPendingResidents() {
        return residentRepository.findResidentByFilter(resident -> !resident.getStatus().equals(PENDING));
    }

    public Optional<Resident> findResidentById(String id) {
        return residentRepository.findResidentById(id);
    }
}
