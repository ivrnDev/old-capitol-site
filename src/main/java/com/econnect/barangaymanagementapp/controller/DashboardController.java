package com.econnect.barangaymanagementapp.controller;

import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.service.ResidentService;
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

    private final LiveReloadUtils liveReloadUtils;
    private final ResidentService residentService;
    private final EmployeeService employeeService;

    public DashboardController(DependencyInjector dependencyInjector) {
        this.residentService = dependencyInjector.getResidentService();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
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
