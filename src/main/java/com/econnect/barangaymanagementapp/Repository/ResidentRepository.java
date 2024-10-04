package com.econnect.barangaymanagementapp.Repository;

import com.econnect.barangaymanagementapp.Config.Config;
import com.econnect.barangaymanagementapp.Domain.Resident;
import com.econnect.barangaymanagementapp.Enumeration.Paths.ApiPath;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;

public class ResidentRepository extends BaseRepository<Resident> {
    private final String apiKey = Config.getFirebaseUrl() + ApiPath.RESIDENTS.getPath();

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
