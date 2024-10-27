package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.MainApplication;
import javafx.fxml.FXMLLoader;

import java.lang.reflect.Constructor;

public class FXMLLoaderFactory {
    private final DependencyInjector dependencyInjector;

    public FXMLLoaderFactory(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
    }

    public FXMLLoader createFXMLLoader(String fxmlPath, Object... constructorArgs) {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlPath));
        System.out.println("FXML Path: " + fxmlPath);
        if (loader.getLocation() == null) {
            throw new IllegalStateException("FXML file not found: " + fxmlPath);
        }
        loader.setControllerFactory(controllerClass -> instantiateController(controllerClass, constructorArgs));
        return loader;
    }

    private Object instantiateController(Class<?> controllerClass, Object[] constructorArgs) {
        try {
            if (constructorArgs != null && constructorArgs.length > 0) {
                for (Constructor<?> constructor : controllerClass.getConstructors()) {
                    if (constructor.getParameterTypes().length == constructorArgs.length) {
                        return constructor.newInstance(constructorArgs);
                    }
                }
            }
            return injectDependenciesToController(controllerClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName(), e);
        }
    }

    private Object injectDependenciesToController(Class<?> controllerClass) {
        try {
            for (Constructor<?> constructor : controllerClass.getConstructors()) {
                if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == DependencyInjector.class) {
                    return constructor.newInstance(dependencyInjector);
                }
            }
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName(), e);
        }
    }
}
