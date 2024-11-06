package com.econnect.barangaymanagementapp.controller.shared.modal;

import com.econnect.barangaymanagementapp.controller.component.BaseViewController;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class ViewResidentController implements BaseViewController {
    private final ModalUtils modalUtils;
    private String residentId;

    @FXML
    private ImageView closeBtn;
    @FXML
    private TextField residentIdInput;

    public ViewResidentController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeView());
        Platform.runLater(() -> {
            residentIdInput.setText(residentId);
        });
    }

    private void closeView() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.residentId = id;
    }
}
