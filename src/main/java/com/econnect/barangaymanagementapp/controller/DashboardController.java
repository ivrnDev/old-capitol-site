package com.econnect.barangaymanagementapp.controller;

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

                int totalCertificates = certificateService.getTotalCertificates();
                int totalCompletedCertificates = certificateService.getAllCompletedCertificates();
                int todayTotalCertificateRequests = certificateService.todayTotalCertificateRequests();
                int totalPendingCertificates = certificateService.totalPendingCertificates();

                int totalBarangayId = barangayidService.totalBarangayId();
                int totalCompletedBarangayId = barangayidService.totalCompletdBarangayId();
                int todayTotalBarangayIdRequests = barangayidService.todayTotalBarangayIdRequests();
                int totalPendingBarangayIdRequests = barangayidService.totalPendingBarangayIdRequests();

                int totalCedula = cedulaService.totalCedulas();
                int totalCompletdCedula = cedulaService.totalCompletdCedula();
                int todayTotalCedulaRequests = cedulaService.todayTotalCedulaRequests();
                int totalPendingCedulaRequests = cedulaService.totalPendingCedulas();

                int totalEvents = eventService.totalEvents();
                int totalCompletedEvents = eventService.totalCompletedEvents();
                int todayTotalEvents = eventService.todayTotalEventRequests();
                int totalPendingEvents = eventService.totalPendingEvents();

                int todayTotalBorrows = borrowService.todayTotalBorrowRequests();
                int totalCompletedBorrows = borrowService.totalCompletedBorrowRequests();

                int totalRequestsCount = totalCertificates + totalBarangayId + totalCedula + totalEvents + todayTotalBorrows;
                int totalCompletedRequests = totalCompletedCertificates + totalCompletedBarangayId + totalCompletdCedula + totalCompletedEvents + totalCompletedBorrows;
                int todayRequestsCount = todayTotalCertificateRequests + todayTotalBarangayIdRequests + todayTotalCedulaRequests + todayTotalEvents + todayTotalBorrows;
                int totalPendingRequests = totalPendingCertificates + totalPendingBarangayIdRequests + totalPendingCedulaRequests + totalPendingEvents;
                Platform.runLater(() -> {
                    totalResident.setText(String.valueOf(verifiedResidentCount));
                    todayResidentApplications.setText(String.valueOf(todayPendingResidentCount));
                    suspendedResident.setText(String.valueOf(suspendedResidentCount));
                    rejectedResident.setText(String.valueOf(rejectedResidentCount));

                    totalEmployee.setText(String.valueOf(activeEmployeeCount));
                    todayEmployeeApplicants.setText(String.valueOf(todayPendingApplicantCount));
                    pendingApplicants.setText(String.valueOf(pendingApplicantCount));
                    processingApplicants.setText(String.valueOf(processingApplicantCount));

                    totalRequests.setText(String.valueOf(totalRequestsCount));
                    completedRequests.setText(String.valueOf(totalCompletedRequests));
                    todayRequests.setText(String.valueOf(todayRequestsCount));
                    pendingRequests.setText(String.valueOf(totalPendingRequests));
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
