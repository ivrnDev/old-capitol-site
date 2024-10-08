package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;

public class ResidentRepository extends BaseRepository<Resident> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.RESIDENTS.getPath();

    public ResidentRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createResident(Resident resident) {
        return create(apiKey + "/" + resident.getId(), resident);
    }

    public Response updateResident(Object object) {
        return null;
    }

    public Boolean deleteResidentById(String id) {
        return null;
    }

    public Optional<Resident> findResidentById(String id) {
        return findById(apiKey, id, new TypeReference<>() {
        });
    }

    public List<Resident> findAllResidents() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }


}
