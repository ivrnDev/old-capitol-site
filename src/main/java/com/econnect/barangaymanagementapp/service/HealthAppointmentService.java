package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.HealthAppointment;
import com.econnect.barangaymanagementapp.repository.HealthAppointmentRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.util.function.Consumer;

public class HealthAppointmentService {
    private final HealthAppointmentRepository cedulaRepository;

    public HealthAppointmentService(DependencyInjector dependencyInjector) {
        this.cedulaRepository = dependencyInjector.getHealthAppointmentRepository();
    }

    public Response createHealthAppointment(HealthAppointment cedula) {
        int baseId = 1000;
        String residentId = cedula.getId();
        int countOfHealthAppointments = findCountOfHealthAppointmentsByResidentId(residentId);
        int autoIncrementId = countOfHealthAppointments > 0 ? baseId + countOfHealthAppointments : baseId;
        cedula.setId(cedula.getId() + "-" + autoIncrementId);
        return cedulaRepository.createHealthAppointment(cedula);
    }

    private int findCountOfHealthAppointmentsByResidentId(String residentId) {
        return (int) cedulaRepository.findHealthAppointmentByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

 /*   public List<HealthAppointment> findAllHealthAppointments() {
        return cedulaRepository.findAllHealthAppointments();
    }

    public List<HealthAppointment> findAllPendingHealthAppointments() {
        return cedulaRepository.findHealthAppointmentByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<HealthAppointment> findHealthAppointmentById(String id) {
        return cedulaRepository.findHealthAppointmentById(id);
    }

    public Optional<HealthAppointment> findCompletedHealthAppointment(String id) {
        return cedulaRepository.findHealthAppointmentById(id).filter(request -> request.getStatus().equals(HealthAppointmentStatus.COMPLETED));
    }

    public Response updateHealthAppointmentByStatus(String requestId, HealthAppointmentStatus status) {
        return cedulaRepository.updateHealthAppointmentByStatus(requestId, status);
    }*/

//    private String generateReferenceNumber() {
//        int OTP_LENGTH = 12;
//        SecureRandom random = new SecureRandom();
//        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
//        return String.format("%012d", otp);
//    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        cedulaRepository.enableLiveReload(handleDataUpdate);
    }
}
