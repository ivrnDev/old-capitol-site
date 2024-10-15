package com.econnect.barangaymanagementapp.controller.shared;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.interfaces.TableRowInterface;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEFAULT_PROFILE;
import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.TABLE_NO_DATA;

public abstract class BaseTableController {

    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;

    private final Map<String, Image> imageCache = new HashMap<>();

    public BaseTableController(DependencyInjector dependencyInjector) {
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    protected abstract void addEmployeeApplicationRow(String employeeId, String lastName, String firstName, StatusType.EmployeeStatus status, ApplicationType type, ZonedDateTime zonedDate, String imageUrl);

    protected Image getImageOrDefault(String employeeId) {
        return imageCache.getOrDefault(employeeId, new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath()))));
    }

    protected <T extends TableRowInterface> void loadImage(String id, String imageUrl, T rowController) {
        if (imageCache.containsKey(id)) {
            rowController.setImage(imageCache.get(id));
        } else {
            Task<Image> loadImageTask = new Task<>() {
                @Override
                protected Image call() {
                    if (imageUrl.isEmpty()) {
                        return new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath())));
                    } else {
                        return new Image(imageUrl);
                    }
                }

                @Override
                protected void succeeded() {
                    Image image = getValue();
                    imageCache.put(id, image);
                    rowController.setImage(image);
                }

                @Override
                protected void failed() {
                    Throwable exception = getException();
                    System.err.println("Error loading image: " + exception.getMessage());
                    Image defaultImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath())));
                    imageCache.put(id, defaultImage);
                    rowController.setImage(defaultImage);
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
            e.printStackTrace();
        }
    }

    public void clearRow() {
        tableContent.getChildren().clear();
    }
}
