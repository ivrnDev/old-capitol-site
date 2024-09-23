package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.Repository.Employee.EmployeeRepository;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Service.LoginService;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DependencyInjector {
    private final Stage stage;
    private final SceneManager sceneManager;
    private final ModalUtils modalUtils;
    private final LoginService loginService;
    private final UserSession userSession;
    private final NavigationState navigationState;
    private final SoundUtils soundUtils;

    public DependencyInjector(Stage stage) {
        this.stage = stage;
        this.soundUtils = new SoundUtils();
        this.navigationState = new NavigationState();
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

    public FXMLLoader getLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlPath));
        loader.setControllerFactory(controllerClass -> injectDependenciesToController(controllerClass));
        return loader;
    }

    private Object injectDependenciesToController(Class<?> controllerClass) {
        try {
            for (Constructor<?> constructor : controllerClass.getConstructors()) {
                Class<?>[] paramTypes = constructor.getParameterTypes();

                if (paramTypes.length == 1 && paramTypes[0] == DependencyInjector.class) {
                    return constructor.newInstance(this);
                }
            }
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName(), e);
        }
    }


}
