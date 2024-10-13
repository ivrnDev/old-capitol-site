package com.econnect.barangaymanagementapp.controller.barangayoffice.modal;

import com.econnect.barangaymanagementapp.controller.component.BaseViewController;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ViewEmployeeApplicationController implements BaseViewController {
    private final ModalUtils modalUtils;
    private Stage currentStage;
    private String employeeId;
    private final ResidentService residentService;

    @FXML
    private HBox viewResume;

    @FXML
    private ImageView profilePicture;

    @FXML
    private TextField firstName;

    public ViewEmployeeApplicationController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        Platform.runLater(() -> this.currentStage = (Stage) viewResume.getScene().getWindow());
    }

    public void initialize() {
        profilePicture.setOnMouseClicked(_ -> handleClickProfile());
        Platform.runLater(() -> {
            firstName.setText(employeeId);
        });
    }

    public void handleClickProfile() {
    }

    @FXML
    private void closeView() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.employeeId = id;
    }
}
