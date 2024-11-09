package com.econnect.barangaymanagementapp.controller.barangayoffice.modal.view;

import com.econnect.barangaymanagementapp.controller.component.BaseViewController;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.service.CertificateService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.util.Optional;

public class ViewDocumentRequest implements BaseViewController {
    private final ModalUtils modalUtils;
    private String certificateId;
    private final ResidentService residentService;
    private final CertificateService certificateService;

    @FXML
    private ImageView closeBtn;
    @FXML
    private ImageView profilePicture;
    @FXML
    private TextField residentIdInput, requestInput, applicationTypeInput, residentTypeInput, typeInput, statusInput, referenceNumberInput;

    public ViewDocumentRequest(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.certificateService = dependencyInjector.getCertificateService();
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeView());
//        profilePicture.setOnMouseClicked(_ -> handleClickProfile());
        populateFields();
    }

    private void populateFields() {
        Task<Optional<Certificate>> requestTask = new Task<>() {
            @Override
            protected Optional<Certificate> call() {
                return certificateService.findCertificateById(certificateId);
            }

            @Override
            protected void succeeded() {
                Optional<Certificate> certificateValue = getValue();
                if (certificateValue.isPresent()) {
                    Certificate certificate = certificateValue.get();
                    populateData(certificate);
//                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), resident.getProfileUrl());
                }
            }


            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };
        new Thread(requestTask).start();
    }

    private void populateData(Certificate certificate) {
        if (certificate == null) return;
        residentIdInput.setText(certificate.getId().substring(0, certificate.getId().length() - 5));
        requestInput.setText(certificate.getRequest());
        applicationTypeInput.setText(certificate.getApplicationType().getName());
        residentTypeInput.setText(certificate.getRequestorType());
        typeInput.setText("Certificate");
        statusInput.setText(certificate.getStatus().getName());
        referenceNumberInput.setText(certificate.getReferenceNumber());
    }

    @FXML
    private void closeView() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.certificateId = id;
    }
}
