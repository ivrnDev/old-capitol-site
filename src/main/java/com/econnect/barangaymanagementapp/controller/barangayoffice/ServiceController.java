package com.econnect.barangaymanagementapp.controller.barangayoffice;

import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class ServiceController {

    @FXML
    private VBox certificateForm, barangayIdForm, cedulaRequestForm, applyWorkForm, eventRequestForm, toolsAndMaterialsForm, assistanceForm, complaintForm, healthForm;

    private final ModalUtils modalUtils;
    private final LiveReloadUtils liveReloadUtils;

    public ServiceController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
    }

    public void initialize() {
        resetLiveReload();
        setupEventListener();
    }

    private void setupEventListener() {
        certificateForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.CERTIFICATE_FORM));
        barangayIdForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.ID_FORM));
        cedulaRequestForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.CEDULA_FORM));
        applyWorkForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.ADD_EMPLOYEE));
        eventRequestForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.EVENT_REQUEST_FORM));
        toolsAndMaterialsForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.TOOL_AND_MATERIALS_FORM));
        assistanceForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.ASSISTANCE_FORM));
        complaintForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.COMPLAINT_FORM));
        healthForm.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.HEALTH_FORM));
    }

    private void resetLiveReload() {
        liveReloadUtils.stopListeningToUpdates();
    }
}
