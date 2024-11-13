package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Patient;
import com.econnect.barangaymanagementapp.repository.PatientRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.util.function.Consumer;

public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(DependencyInjector dependencyInjector) {
        this.patientRepository = dependencyInjector.getPatientRepository();
    }

    public Response createPatient(Patient patient) {
        patient.setId(patient.getId());
        patient.setStatus("PENDING");
        return patientRepository.createPatient(patient);
    }

   /* public List<Patient> findAllPatients() {
        return patientRepository.findAllPatients();
    }

    public List<Patient> findAllPendingPatients() {
        return patientRepository.findPatientByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<Patient> findPatientById(String id) {
        return patientRepository.findPatientById(id);
    }

    private int findCountOfPatientsByResidentId(String residentId) {
        return (int) patientRepository.findPatientByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public Optional<Patient> findCompletedPatient(String id) {
        return patientRepository.findPatientById(id).filter(request -> request.getStatus().equals(PatientStatus.COMPLETED));
    }*/

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        patientRepository.enableLiveReload(handleDataUpdate);
    }
}
