package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Cedula;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.CedulaStatus;
import com.econnect.barangaymanagementapp.repository.CedulaRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.CedulaStatus.COMPLETED;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.CedulaStatus.PENDING;

public class CedulaService {
    private final CedulaRepository cedulaRepository;

    public CedulaService(DependencyInjector dependencyInjector) {
        this.cedulaRepository = dependencyInjector.getCedulaRepository();
    }

    public Response createCedula(Cedula cedula) {
        int baseId = 1000;
        String residentId = cedula.getId();
        int countOfCedulas = findCountOfCedulasByResidentId(residentId);
        int autoIncrementId = countOfCedulas > 0 ? baseId + countOfCedulas : baseId;
        cedula.setId(cedula.getId() + "-" + autoIncrementId);
        cedula.setReferenceNumber(generateReferenceNumber());
        cedula.setCreatedAt(ZonedDateTime.now());
        cedula.setUpdatedAt(ZonedDateTime.now());
        cedula.setApplicationType(ApplicationType.WALK_IN);
        cedula.setStatus(CedulaStatus.PENDING);
        return cedulaRepository.createCedula(cedula);
    }

    public List<Cedula> findAllCedulas() {
        return cedulaRepository.findAllCedulas();
    }

    public List<Cedula> findAllPendingCedulas() {
        return cedulaRepository.findCedulaByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<Cedula> findCedulaById(String id) {
        return cedulaRepository.findCedulaById(id);
    }

    private int findCountOfCedulasByResidentId(String residentId) {
        return (int) cedulaRepository.findCedulaByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public Optional<Cedula> findCompletedCedula(String id) {
        return cedulaRepository.findCedulaById(id).filter(request -> request.getStatus().equals(CedulaStatus.COMPLETED));
    }

    public Response updateCedulaByStatus(String requestId, CedulaStatus status) {
        return cedulaRepository.updateCedulaByStatus(requestId, status);
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }

    //Analytics
    public int totalCedulas() {
        return cedulaRepository.findAllCedulas().size();
    }

    public int totalCedulaRequests() {
        return cedulaRepository.findCedulaByFilter(request -> !request.getStatus().equals(COMPLETED)).size();
    }


    public int totalPendingCedulas() {
        return cedulaRepository.findCedulaByFilter(request -> request.getStatus().equals(PENDING)).size();
    }

    public int todayTotalCedulaRequests() {
        return cedulaRepository.findCedulaByFilter(request -> request.getCreatedAt().toLocalDate().equals(LocalDate.now())).size();
    }

    public int totalProcessingCedulas() {
        return cedulaRepository.findCedulaByFilter(request -> request.getStatus().equals(CedulaStatus.IN_PROGRESS)).size();
    }

    public int totalCompletdCedula() {
        return cedulaRepository.findCedulaByFilter(request -> request.getStatus().equals(COMPLETED)).size();
    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        cedulaRepository.enableLiveReload(handleDataUpdate);
    }
}
