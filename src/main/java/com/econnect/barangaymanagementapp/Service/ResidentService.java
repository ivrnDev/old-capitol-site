package com.econnect.barangaymanagementapp.Service;

import com.econnect.barangaymanagementapp.Domain.Resident;
import com.econnect.barangaymanagementapp.Repository.ResidentRepository;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;

public class ResidentService {
    private final ResidentRepository residentRepository;

    public ResidentService(DependencyInjector dependencyInjector) {
        this.residentRepository = dependencyInjector.getResidentRepository();
    }

    public Response createResident(Resident resident) {
        return residentRepository.createResident(resident);
    }

    public List<Resident> findAllResidents() {
        return residentRepository.findAllResidents();
    }

    public Optional<Resident> findResidentById(String id) {
        return residentRepository.findResidentById(id);
    }
}
