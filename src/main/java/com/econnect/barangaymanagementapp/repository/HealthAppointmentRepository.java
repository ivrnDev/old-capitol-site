package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.HealthAppointment;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class HealthAppointmentRepository extends BaseRepository<HealthAppointment> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.HEALTH_APPOINTMENT.getPath();

    public HealthAppointmentRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createHealthAppointment(HealthAppointment appointment) {
        return create(apiKey + "/" + appointment.getId(), appointment);
    }

    public Response updateHealthAppointment(HealthAppointment appointment) {
        return create(apiKey + "/" + appointment.getId(), appointment);

    }

    public Boolean deleteHealthAppointmentById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<HealthAppointment> findHealthAppointmentById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<HealthAppointment> findAllHealthAppointments() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<HealthAppointment> findHealthAppointmentByFilter(Predicate<HealthAppointment> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

//    public Response updateHealthAppointmentByStatus(String requestId, StatusType.HealthAppointmentStatus status) {
//        return updateBy(apiKey, requestId, new TypeReference<>() {
//        }, request -> request.setStatus(status));
//    }

    public void enableLiveReload(Consumer<String> handleDataUpdates) {
        enableLiveReload(apiKey, handleDataUpdates, "CEDULA:");
    }

}
