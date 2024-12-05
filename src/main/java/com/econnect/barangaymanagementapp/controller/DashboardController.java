package com.econnect.barangaymanagementapp.controller;

import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DashboardController {
    @FXML
    private VBox contentPane;
    @FXML
    private Text totalResident, pendingResident, suspendedResident, rejectedResident;
    @FXML
    private Text totalEmployee, totalApplicants, pendingApplicants, processingApplicants;

    private final ResidentService residentService;

    public DashboardController(DependencyInjector dependencyInjector) {
        this.residentService = dependencyInjector.getResidentService();
    }

    public void initialize() {
        fetchData();
    }

    private void fetchData() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    totalResident.setText(String.valueOf(residentService.getVerifiedResident()));
                    pendingResident.setText(String.valueOf(residentService.getPendingResident()));
                    suspendedResident.setText(String.valueOf(residentService.getSuspendedResident()));
                    rejectedResident.setText(String.valueOf(residentService.getRejectedResident()));
                });
                return null;
            }
        };
        new Thread(task).start();

    }


}
