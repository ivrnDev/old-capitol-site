package com.econnect.barangaymanagementapp.controller.base;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEFAULT_PROFILE;
import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.TABLE_NO_DATA;

public abstract class BaseTableController<T> {

    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final Map<String, Image> imageCache = new HashMap<>();

    public BaseTableController(DependencyInjector dependencyInjector) {
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    public abstract void addRow(T data);

    public void showNoData() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(TABLE_NO_DATA.getFxmlPath());
            Parent noDataRow = loader.load();
            tableContent.getChildren().add(noDataRow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeNoDataRow() {
        tableContent.getChildren().removeIf(node -> node.getId().equals("noData"));
    }

    protected Image getImageOrDefault(String employeeId) {
        return imageCache.getOrDefault(employeeId, new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath()))));
    }


    protected <C extends BaseRowController<T>> void loadImage(String id, String imageUrl, C rowController, boolean forceLoad) {
        if (!forceLoad && imageCache.containsKey(id)) {
            rowController.setImage(imageCache.get(id));
            return;
        }

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

    public void clearRow() {
        tableContent.getChildren().clear();
    }
}
