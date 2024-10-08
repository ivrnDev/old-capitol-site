package com.econnect.barangaymanagementapp.controller.humanresources.modal;

import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ViewController {
    private final ModalUtils modalUtils;
    private Stage currentStage;

    @FXML
    private ImageView resumeImage;

    public ViewController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        Platform.runLater(() -> this.currentStage = (Stage) resumeImage.getScene().getWindow());
    }

    public void initialize() {
        resumeImage.setOnMouseClicked(_ -> handleClickResume());
    }

    @FXML
    public void handleClickResume() {
        modalUtils.showImageView(resumeImage.getImage(), currentStage);
    }

    @FXML
    private void closeView() {
        modalUtils.closeCustomizeModal();
    }
}
