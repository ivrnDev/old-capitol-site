package com.econnect.barangaymanagementapp.Controller.HumanResources.Table.Application;

import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.Roles;
import com.econnect.barangaymanagementapp.Enumeration.Status;
import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class ApplicationTableController {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;

    public ApplicationTableController(DependencyInjector dependencyInjector) {
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
    }

    public void addApplicationRow(String residentId, String lastName, String firstName, Status.EmployeeStatus status, String type, LocalDateTime createdAt, String imageUrl) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader("View/HumanResources/Table/application-row.fxml");
            loader.setController(new ApplicationRowController(dependencyInjector));
            HBox applicationRow = loader.load();
            ApplicationRowController rowController = loader.getController();
            rowController.setApplicationData(
                    residentId,
                    lastName,
                    firstName,
                    status.getName(),
                    type,
                    LocalDateTime.parse(createdAt.toString()).toLocalDate().toString(),
                    LocalDateTime.parse(createdAt.toString()).toLocalTime().toString(),
                    imageUrl.isEmpty() ? new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/default-profile.png"))) : new Image(imageUrl)
            );
            tableContent.getChildren().add(applicationRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding application row: " + e.getMessage(), e);
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
}
