package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Mapper.EmployeeMapper;
import com.econnect.barangaymanagementapp.Repository.Employee.EmployeeRepository;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Service.LoginService;
import javafx.stage.Stage;

public class DependencyInjector {
    private final Stage stage;
    private final SceneManager sceneManager;
    private final UserSession userSession;
    private final NavigationState navigationState;
    private final SoundUtils soundUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final ModalUtils modalUtils;
    private final ButtonUtils buttonUtils;
    private final JsonConverter jsonConverter;
    private final HTTPClient httpClient;
    private final PasswordUtils passwordUtils;

    private final EmployeeMapper employeeMapper;

    private final EmployeeRepository employeeRepository;

    private final LoginService loginService;
    private final EmployeeService employeeService;

    public DependencyInjector(Stage stage) {
        this.stage = stage;
        this.soundUtils = new SoundUtils();
        this.navigationState = new NavigationState();
        this.fxmlLoaderFactory = new FXMLLoaderFactory(this);
        this.userSession = UserSession.getInstance();
        this.sceneManager = new SceneManager(this);
        this.modalUtils = new ModalUtils(this);
        this.buttonUtils = new ButtonUtils();
        this.jsonConverter = new JsonConverter();
        this.httpClient = new HTTPClient();
        this.passwordUtils = new PasswordUtils();

        this.employeeMapper = new EmployeeMapper();

        this.employeeRepository = new EmployeeRepository(this);
        this.employeeService = new EmployeeService(this);
        this.loginService = new LoginService(this);
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

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public EmployeeRepository getEmployeeRepository() {
        return employeeRepository;
    }

    public FXMLLoaderFactory getFxmlLoaderFactory() {
        return fxmlLoaderFactory;
    }

    public ButtonUtils getButtonUtils() {
        return buttonUtils;
    }

    public JsonConverter getJsonConverter() {
        return jsonConverter;
    }

    public HTTPClient getHttpClient() {
        return httpClient;
    }

    public PasswordUtils getPasswordEncryption() {
        return passwordUtils;
    }

    public EmployeeMapper getEmployeeMapper() {
        return employeeMapper;
    }
}
