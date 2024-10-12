package com.econnect.barangaymanagementapp.controller;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.service.LoginService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import com.econnect.barangaymanagementapp.util.SceneManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {

    @FXML
    private VBox loginContainer;

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
//        Task<Optional<Employee>> loginTask = new Task<Optional<Employee>>() {
//            @Override
//            protected Optional<Employee> call() {
//                return loginService.login(usernameInput.getText(), passwordInput.getText());
//            }
//
//            @Override
//            protected void succeeded() {
//                Optional<Employee> loggedEmployee = loginService.login(usernameInput.getText(), passwordInput.getText());
//                if (loggedEmployee != null && loggedEmployee.isPresent()) {
//                    DepartmentType loggedEmployeeDepartment = loggedEmployee.get().getDepartment();
//                    sceneManager.switchScene(loggedEmployeeDepartment.getLink());
//                } else {
//                    triggerError(true);
//                }
//            }
//
//            @Override
//            protected void failed() {
//                modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while logging in.");
//            }
//
//        };
//
//        new Thread(loginTask).start();

        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(loginContainer.getWidth(), loginContainer.getHeight());
        Platform.runLater(() -> loginContainer.getChildren().add(loadingIndicator));
        
        Runnable call = () -> {
            Optional<Employee> loggedEmployee = loginService.login(usernameInput.getText(), passwordInput.getText());

            Platform.runLater(() -> {
                loginContainer.getChildren().remove(loadingIndicator);
                if (loggedEmployee != null && loggedEmployee.isPresent()) {
                    DepartmentType loggedEmployeeDepartment = loggedEmployee.get().getDepartment();
                    sceneManager.switchScene(loggedEmployeeDepartment.getLink());
                } else {
                    triggerError(true);
                }
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> loginContainer.getChildren().remove(loadingIndicator));
            System.err.println("Error loading employees");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    @FXML
    private void handleCloseButton() {
        modalUtils.showModal(Modal.CLASSIC, "Confirm Exit", "Are you sure you want to exit?", isConfirmed -> {
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
