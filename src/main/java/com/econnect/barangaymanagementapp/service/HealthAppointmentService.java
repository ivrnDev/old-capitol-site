package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.HealthAppointment;
import com.econnect.barangaymanagementapp.repository.HealthAppointmentRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.util.function.Consumer;

public class HealthAppointmentService {
    private final HealthAppointmentRepository appointmentRepository;

    public HealthAppointmentService(DependencyInjector dependencyInjector) {
        this.appointmentRepository = dependencyInjector.getHealthAppointmentRepository();
    }

    public Response createHealthAppointment(HealthAppointment appointment) {
        int baseId = 1000;
        String residentId = appointment.getId();
        int countOfHealthAppointments = findCountOfHealthAppointmentsByResidentId(residentId);
        int autoIncrementId = countOfHealthAppointments > 0 ? baseId + countOfHealthAppointments : baseId;
        appointment.setId(appointment.getId() + "-" + autoIncrementId);
        appointment.setStatus("PENDING");
        return appointmentRepository.createHealthAppointment(appointment);
    }

    private int findCountOfHealthAppointmentsByResidentId(String residentId) {
        return (int) appointmentRepository.findHealthAppointmentByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

 /*   public List<HealthAppointment> findAllHealthAppointments() {
        return appointmentRepository.findAllHealthAppointments();
    }

    public List<HealthAppointment> findAllPendingHealthAppointments() {
        return appointmentRepository.findHealthAppointmentByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<HealthAppointment> findHealthAppointmentById(String id) {
        return appointmentRepository.findHealthAppointmentById(id);
    }

    public Optional<HealthAppointment> findCompletedHealthAppointment(String id) {
        return appointmentRepository.findHealthAppointmentById(id).filter(request -> request.getStatus().equals(HealthAppointmentStatus.COMPLETED));
    }

    public Response updateHealthAppointmentByStatus(String requestId, HealthAppointmentStatus status) {
        return appointmentRepository.updateHealthAppointmentByStatus(requestId, status);
    }*/

//    private String generateReferenceNumber() {
//        int OTP_LENGTH = 12;
//        SecureRandom random = new SecureRandom();
//        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
//        return String.format("%012d", otp);
//    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        appointmentRepository.enableLiveReload(handleDataUpdate);
    }
}
