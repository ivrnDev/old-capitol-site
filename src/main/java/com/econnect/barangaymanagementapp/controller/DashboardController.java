package com.econnect.barangaymanagementapp.controller;

import com.econnect.barangaymanagementapp.domain.Cedula;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.service.*;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DashboardController {
    @FXML
    private VBox contentPane;
    @FXML
    private Text totalResident, todayResidentApplications, suspendedResident, rejectedResident;
    @FXML
    private Text totalEmployee, todayEmployeeApplicants, pendingApplicants, processingApplicants;
    @FXML
    private Text completedRequests, totalRequests, todayRequests, pendingRequests;

    private final LiveReloadUtils liveReloadUtils;
    private final ResidentService residentService;
    private final EmployeeService employeeService;
    private final CertificateService certificateService;
    private final BarangayidService barangayidService;
    private final CedulaService cedulaService;
    private final EventService eventService;
    private final BorrowService borrowService;

    public DashboardController(DependencyInjector dependencyInjector) {
        this.residentService = dependencyInjector.getResidentService();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
        this.certificateService = dependencyInjector.getCertificateService();
        this.barangayidService = dependencyInjector.getBarangayidService();
        this.cedulaService = dependencyInjector.getCedulaService();
        this.eventService = dependencyInjector.getEventService();
        this.borrowService = dependencyInjector.getBorrowService();
    }

    public void initialize() {
        fetchData();
        resetLiveReload();
    }

    private void fetchData() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                int verifiedResidentCount = residentService.getVerifiedResident();
                int todayPendingResidentCount = residentService.getTodayApplicants();
                int suspendedResidentCount = residentService.getSuspendedResident();
                int rejectedResidentCount = residentService.getRejectedResident();

                int activeEmployeeCount = employeeService.getActiveEmployees();
                int todayPendingApplicantCount = employeeService.getTodayPendingApplicants();
                int pendingApplicantCount = employeeService.getPendingApplicants();
                int processingApplicantCount = employeeService.getProcessingApplicants();

                int totalCertificateRequests = certificateService.getTotalCertificates();
                int todayCertificateRequests = certificateService.todayCertificateRequests();
                int totalProcessingCertificates = certificateService.totalProcessingCertificates();
                int totalPendingCertificates = certificateService.totalPendingCertificates();

                int totalBarangayIdRequests = barangayidService.totalBarangayId();
                int todayBarangayIdRequests = barangayidService.todayBarangayIdRequests();
                int totalPendingBarangayIdRequests = barangayidService.totalPendingBarangayIdRequests();
                int totalProcessingBarangayIdRequests = barangayidService.totalProcessingBarangayIdRequests();

                int totalCedula = cedulaService.totalCedulas();
                int totalCedulaRequests = cedulaService.totalCedulaRequests();
                int todayCedulaRequests = cedulaService.todayCedulaRequests();
                int totalPendingCedulaRequests = cedulaService.totalPendingCedulas();
                int totalProcessingCedulaRequests = cedulaService.totalProcessingCedulas();


                Platform.runLater(() -> {
                    totalResident.setText(String.valueOf(verifiedResidentCount));
                    todayResidentApplications.setText(String.valueOf(todayPendingResidentCount));
                    suspendedResident.setText(String.valueOf(suspendedResidentCount));
                    rejectedResident.setText(String.valueOf(rejectedResidentCount));

                    totalEmployee.setText(String.valueOf(activeEmployeeCount));
                    todayEmployeeApplicants.setText(String.valueOf(todayPendingApplicantCount));
                    pendingApplicants.setText(String.valueOf(pendingApplicantCount));
                    processingApplicants.setText(String.valueOf(processingApplicantCount));
                });
                return null;
            }
        };
        new Thread(task).start();
    }


    private void resetLiveReload() {
        liveReloadUtils.stopListeningToUpdates();
    }

}
