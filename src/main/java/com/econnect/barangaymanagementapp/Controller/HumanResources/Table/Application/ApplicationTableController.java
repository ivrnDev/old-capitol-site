package com.econnect.barangaymanagementapp.Controller.HumanResources.Table.Application;

import com.econnect.barangaymanagementapp.Controller.HumanResources.Table.Employee.EmployeeRowController;
import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.Roles;
import com.econnect.barangaymanagementapp.Enumeration.Status;
import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.FXMLLoaderFactory;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplicationTableController {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;

    private final Map<String, Image> imageCache = new HashMap<>();

    public ApplicationTableController(DependencyInjector dependencyInjector) {
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
    }

    public void addEmployeeRow(String employeeId, String lastName, String firstName, String status, String date, String time, String imageUrl) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader("View/HumanResources/Table/Application/application-row.fxml");
            loader.setController(new ApplicationRowController(dependencyInjector));
            HBox applicationRow = loader.load();
            ApplicationRowController applicationRowController = loader.getController();
            applicationRowController.setEmployeeData(employeeId, lastName, firstName, status, date, time, getImageOrDefault(employeeId));
            loadEmployeeImage(employeeId, imageUrl, applicationRowController);
            tableContent.getChildren().add(applicationRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding employee row: " + e.getMessage(), e);
        }
    }

    private Image getImageOrDefault(String employeeId) {
        return imageCache.getOrDefault(employeeId, new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/default-profile.png"))));
    }

    private void loadEmployeeImage(String employeeId, String imageUrl, ApplicationRowController employeeRowController) {
        if (imageCache.containsKey(employeeId)) {
            employeeRowController.setProfileImage(imageCache.get(employeeId));
        } else {
            Task<Image> loadImageTask = new Task<>() {
                @Override
                protected Image call() {
                    if (imageUrl.isEmpty()) {
                        return new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/default-profile.png")));
                    } else {
                        return new Image(imageUrl);
                    }
                }

                @Override
                protected void succeeded() {
                    Image image = getValue();
                    imageCache.put(employeeId, image);
                    employeeRowController.setProfileImage(image);
                }

                @Override
                protected void failed() {
                    Throwable exception = getException();
                    System.err.println("Error loading image: " + exception.getMessage());
                    Image defaultImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/default-profile.png")));
                    imageCache.put(employeeId, defaultImage);
                    employeeRowController.setProfileImage(defaultImage);
                }
            };

            new Thread(loadImageTask).start();
        }
    }

    public void showNoData() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader("View/Component/no-data-row.fxml");
            Parent noDataRow = loader.load();
            tableContent.getChildren().add(noDataRow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearTable() {
        tableContent.getChildren().clear();
    }
}
