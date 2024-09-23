package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Repository.Employee.EmployeeRepository;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Service.LoginService;
import javafx.stage.Stage;

public class DependencyInjector {
    private final Stage stage;
    private final SceneManager sceneManager;
    private final ModalUtils modalUtils;
    private final LoginService loginService;
    private final UserSession userSession;
    private final NavigationState navigationState;
    private final SoundUtils soundUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;

    public DependencyInjector(Stage stage) {
        this.stage = stage;
        this.soundUtils = new SoundUtils();
        this.navigationState = new NavigationState();
        this.fxmlLoaderFactory = new FXMLLoaderFactory(this);
        this.sceneManager = new SceneManager(this);
        this.modalUtils = new ModalUtils(this);
        EmployeeRepository employeeRepository = new EmployeeRepository();
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        this.userSession = UserSession.getInstance();
        this.loginService = new LoginService(employeeService, userSession);
    }

    public Stage getStage() {
        return stage;
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

    public UserSession getUserSession() {
        return userSession;
    }

    public NavigationState getNavigationState() {
        return navigationState;
    }

    public SoundUtils getSoundUtils() {
        return soundUtils;
    }

    public FXMLLoaderFactory getFxmlLoaderFactory() {
        return fxmlLoaderFactory;
    }

}
