package com.econnect.barangaymanagementapp.Controller;

import com.econnect.barangaymanagementapp.Interface.ControllerDependencies;
import com.econnect.barangaymanagementapp.Service.LoginService;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import com.econnect.barangaymanagementapp.Utils.SceneManager;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController implements ControllerDependencies {

    @FXML
    private ImageView closeBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Text errorLabel;


    private SceneManager sceneManager;
    private ModalUtils modalUtils;
    private LoginService loginService;

    @Override
    public void setDependencies(DependencyInjector dependencyInjector) {
        this.sceneManager = dependencyInjector.getSceneManager();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.loginService = dependencyInjector.getLoginService();
    }

    @FXML
    public void initialize() {
        triggerError(false);
        handleInputEvent();
    }

    @FXML
    private void handleLoginButton() {
        if (isLogged()) {
            sceneManager.switchScene("View/HR/dashboard.fxml");
            return;
        }
        triggerError(true);
    }

    @FXML
    private void handleCloseButton() {
        modalUtils.showConfirmationModal("Confirm Exit?", "Are you sure you want to exit?", isConfirmed -> {
            if (isConfirmed) {
                Stage stage = (Stage) closeBtn.getScene().getWindow();
                stage.close();
            }
        });
    }

    private void handleInputEvent() {
        EventHandler<KeyEvent> keyPressHandler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLoginButton();
                return;
            }
            triggerError(false);
        };

        usernameInput.setOnKeyPressed(keyPressHandler);
        passwordInput.setOnKeyPressed(keyPressHandler);
    }


    private boolean isLogged() {
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        return loginService.login(username, password);
    }


    private void triggerError(boolean value) {
        errorLabel.setManaged(value);
        errorLabel.setVisible(value);
        if (value) {
            usernameInput.setStyle("-fx-border-color: red;");
            passwordInput.setStyle("-fx-border-color: red;");
            return;
        }
        usernameInput.setStyle(null);
        passwordInput.setStyle(null);
    }


}
