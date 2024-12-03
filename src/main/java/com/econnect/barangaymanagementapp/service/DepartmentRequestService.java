package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.DepartmentRequest;
import com.econnect.barangaymanagementapp.repository.DepartmentRequestRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DepartmentRequestService {
    private final DepartmentRequestRepository departmentRequestRepository;

    public DepartmentRequestService(DependencyInjector dependencyInjector) {
        this.departmentRequestRepository = dependencyInjector.getDepartmentRequestRepository();
    }

    public List<DepartmentRequest> findAllDepartmentRequests() {
        return departmentRequestRepository.findAllDepartmentRequests();
    }

    public List<DepartmentRequest> findAllPendingDepartmentRequests() {
        return departmentRequestRepository.findDepartmentRequestByFilter(request -> request.getStatus().equals("Pending"));
    }

    public Optional<DepartmentRequest> findDepartmentRequestById(String id) {
        return departmentRequestRepository.findDepartmentRequestById(id);
    }

    private int findCountOfDepartmentRequestsByResidentId(String residentId) {
        return (int) departmentRequestRepository.findDepartmentRequestByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public Optional<DepartmentRequest> findCompletedDepartmentRequest(String id) {
        return departmentRequestRepository.findDepartmentRequestById(id).filter(request -> request.getStatus().equals("Completed"));
    }

    public Response updateDepartmentRequestByStatus(String requestId, String status) {
        return departmentRequestRepository.updateDepartmentRequestByStatus(requestId, status);
    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        departmentRequestRepository.enableLiveReload(handleDataUpdate);
    }
}
