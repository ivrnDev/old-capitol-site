package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Patient;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PatientRepository extends BaseRepository<Patient> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.ACCOUNTS.getPath();

    public PatientRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createPatient(Patient patient) {
        return create(apiKey + "/" + patient.getId(), patient);
    }

    public Response updatePatient(Patient patient) {
        return create(apiKey + "/" + patient.getId(), patient);

    }

    public Boolean deletePatientById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<Patient> findPatientById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<Patient> findAllPatients() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<Patient> findPatientByFilter(Predicate<Patient> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public void enableLiveReload(Consumer<String> handleDataUpdates) {
        enableLiveReload(apiKey, handleDataUpdates, "CERTIFICATE:");
    }

}
