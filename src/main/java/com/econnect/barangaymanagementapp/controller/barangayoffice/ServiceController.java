package com.econnect.barangaymanagementapp.controller.barangayoffice;

import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class ServiceController {

    @FXML
    private VBox certificateForm, residencyForm, clearanceForm, indigencyForm, censusForm;

    private final ModalUtils modalUtils;

    public ServiceController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    public void initialize() {
        setupEventListener();
    }

    private void setupEventListener() {
        certificateForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.CERTIFICATE_FORM));
        residencyForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.RESIDENCY_FORM));
        clearanceForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.CLEARANCE_FORM));
        indigencyForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.INDIGENCY_FORM));
        censusForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.CENSUS_FORM));
    }
}
