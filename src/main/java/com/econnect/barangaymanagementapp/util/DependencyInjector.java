package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.repository.EmployeeRepository;
import com.econnect.barangaymanagementapp.repository.ResidentRepository;
import com.econnect.barangaymanagementapp.service.EmployeeService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.LoginService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.data.JsonConverter;
import com.econnect.barangaymanagementapp.util.data.PasswordUtils;
import com.econnect.barangaymanagementapp.util.resource.SoundUtils;
import com.econnect.barangaymanagementapp.util.state.NavigationState;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.ButtonUtils;
import com.econnect.barangaymanagementapp.util.ui.FileChooserUtils;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
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
    private final ImageService imageService;
    private final FileChooserUtils fileChooserUtils;
    ;


    private final EmployeeRepository employeeRepository;
    private final ResidentRepository residentRepository;

    private final LoginService loginService;
    private final EmployeeService employeeService;
    private final ResidentService residentService;

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
        this.imageService = new ImageService(this);
        this.fileChooserUtils = new FileChooserUtils();

        this.employeeRepository = new EmployeeRepository(this);
        this.residentRepository = new ResidentRepository(this);

        this.employeeService = new EmployeeService(this);
        this.residentService = new ResidentService(this);
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

    public ResidentRepository getResidentRepository() {
        return residentRepository;
    }

    public ResidentService getResidentService() {
        return residentService;
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

    public PasswordUtils getPasswordUtils() {
        return passwordUtils;
    }

    public ImageService getImageService() {
        return imageService;
    }

    public FileChooserUtils getFileChooser() {
        return fileChooserUtils;
    }
}