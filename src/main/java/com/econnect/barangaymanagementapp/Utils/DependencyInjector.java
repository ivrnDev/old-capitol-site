package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Interface.ControllerDependencies;
import com.econnect.barangaymanagementapp.Repository.Employee.EmployeeRepository;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Service.LoginService;
import javafx.stage.Stage;

public class DependencyInjector {
    private final SceneManager sceneManager;
    private final ModalUtils modalUtils;
    private final LoginService loginService;

    public DependencyInjector(Stage stage) {
        this.sceneManager = new SceneManager(stage, this);
        this.modalUtils = new ModalUtils(stage);
        this.loginService = new LoginService(new EmployeeService(new EmployeeRepository()), UserSession.getInstance());
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public ModalUtils getModalUtils() {
        return modalUtils;
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public void injectDependenciesToController(Object controller) {
        if (controller instanceof ControllerDependencies) {
            ((ControllerDependencies) controller).setDependencies(this);
        }
    }
}
