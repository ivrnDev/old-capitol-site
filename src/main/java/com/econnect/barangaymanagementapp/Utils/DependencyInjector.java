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
    private final SceneManager sceneManager;
    private final ModalUtils modalUtils;
    private final LoginService loginService;
    private final UserSession userSession;

    public DependencyInjector(Stage stage) {
        this.sceneManager = new SceneManager(stage, this);
        this.modalUtils = new ModalUtils(stage);
        EmployeeRepository employeeRepository = new EmployeeRepository();
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        this.userSession = UserSession.getInstance();
        this.loginService = new LoginService(employeeService, userSession);

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
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName(), e);
        }
    }
}
