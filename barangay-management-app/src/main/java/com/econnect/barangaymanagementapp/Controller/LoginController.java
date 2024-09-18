package com.econnect.barangaymanagementapp.Controller;

import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import com.econnect.barangaymanagementapp.Utils.SceneManager.SceneManager;
import com.econnect.barangaymanagementapp.Utils.SceneManager.SceneManagerInjection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController implements SceneManagerInjection {

    @FXML
    private ImageView closeBtn;

    @FXML
    private Button loginBtn;

    private SceneManager sceneManager;

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
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
        ModalUtils.showConfirmationModal("Confirm Exit?", "Are you sure you want to exit?", isConfirmed -> {
            if (isConfirmed) closeWindow();
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }


}
