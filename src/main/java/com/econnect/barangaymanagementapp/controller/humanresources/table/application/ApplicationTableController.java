package com.econnect.barangaymanagementapp.controller.humanresources.table.application;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.econnect.barangaymanagementapp.enumeration.path.fxmlPath.EMPLOYEE_APPLICATION_ROW;
import static com.econnect.barangaymanagementapp.enumeration.path.fxmlPath.TABLE_NO_DATA;

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

    public void addEmployeeRow(String employeeId, String lastName, String firstName, EmployeeStatus status, ApplicationType type, ZonedDateTime zonedDate, String imageUrl) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(EMPLOYEE_APPLICATION_ROW.getFxmlPath());
            loader.setController(new ApplicationRowController(dependencyInjector));
            HBox applicationRow = loader.load();
            ApplicationRowController applicationRowController = loader.getController();
            applicationRowController.setEmployeeData(employeeId, lastName, firstName, status.getName(), type.getName(), DateFormatter.extractDateAndFormat(zonedDate), DateFormatter.extractTimeAndFormat(zonedDate), getImageOrDefault(employeeId));
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
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(TABLE_NO_DATA.getFxmlPath());
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
