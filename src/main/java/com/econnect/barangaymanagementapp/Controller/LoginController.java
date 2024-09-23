package com.econnect.barangaymanagementapp.Controller;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.Service.LoginService;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import com.econnect.barangaymanagementapp.Utils.SceneManager;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {

    @FXML
    private ImageView closeBtn;

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Text errorLabel;

    private final SceneManager sceneManager;
    private final ModalUtils modalUtils;
    private final LoginService loginService;

    public LoginController(DependencyInjector dependencyInjector) {
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
        Optional<Employee> loggedEmployee = loginService.login(usernameInput.getText(), passwordInput.getText());
        if (loggedEmployee != null && loggedEmployee.isPresent()) {
            Departments loggedEmployeeDepartment = loggedEmployee.get().getDepartment();
            sceneManager.switchScene(loggedEmployeeDepartment.getLink());
            return;
        }
        triggerError(true);
    }

    @FXML
    private void handleCloseButton() {
        modalUtils.showModal(Modal.CLASSIC, "Confirm Exit?", "Are you sure you want to exit?", isConfirmed -> {
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
