package com.econnect.barangaymanagementapp.Controller.HumanResources.Table.Employee;

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

public class EmployeeTableController {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;

    private final Map<String, Image> imageCache = new HashMap<>();

    public EmployeeTableController(DependencyInjector dependencyInjector) {
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
    }

    public void addEmployeeRow(String employeeId, String lastName, String firstName, Roles role, Departments department, Status.EmployeeStatus status, String imageUrl) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader("View/HumanResources/Table/Employee/employee-row.fxml");
            loader.setController(new EmployeeRowController(dependencyInjector));
            HBox employeeRow = loader.load();
            EmployeeRowController employeeRowController = loader.getController();
            employeeRowController.setEmployeeData(employeeId, lastName, firstName, role.getName(), department.getName(), status.getName(), getImageOrDefault(employeeId));
            loadEmployeeImage(employeeId, imageUrl, employeeRowController);
            tableContent.getChildren().add(employeeRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding employee row: " + e.getMessage(), e);

        }
    }

    private Image getImageOrDefault(String employeeId) {
        return imageCache.getOrDefault(employeeId, new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/default-profile.png"))));
    }

    private void loadEmployeeImage(String employeeId, String imageUrl, EmployeeRowController employeeRowController) {
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
                    imageCache.put(employeeId, defaultImage);  // Cache the default image
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
