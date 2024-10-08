package com.econnect.barangaymanagementapp.Controller.HumanResources.Modal;

import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
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
