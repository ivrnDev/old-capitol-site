package com.econnect.barangaymanagementapp.Controller;

import com.econnect.barangaymanagementapp.Interface.ControllerDependencies;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import com.econnect.barangaymanagementapp.Utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController implements ControllerDependencies {

    @FXML
    private ImageView closeBtn;

    @FXML
    private Button loginBtn;

    private SceneManager sceneManager;
    private ModalUtils modalUtils;

    @Override
    public void setDependencies(DependencyInjector dependencyInjector) {
        this.sceneManager = dependencyInjector.getSceneManager();
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    @FXML
    public void initialize() {
        loginBtn.setOnMouseClicked(e -> handleLoginButton());
        closeBtn.setOnMouseClicked(e -> handleCloseButton());
    }

    @FXML
    private void handleLoginButton() {
        sceneManager.switchScene("View/dashboard.fxml");
    }

    private void handleCloseButton() {
        modalUtils.showConfirmationModal("Confirm Exit?", "Are you sure you want to exit?", isConfirmed -> {
            if (isConfirmed) closeWindow();
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }


}
