package com.econnect.barangaymanagementapp.controller.shared;

import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public abstract class BaseRowController<T> {
    private final ModalUtils modalUtils;
    private final Stage parentStage;


    @FXML
    private HBox tableRow, buttonContainer;

    @FXML
    private ImageView image;

    public BaseRowController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
    }

    public void initialize() {
        setupImageClick();
        setupRowClickEvents();
        Platform.runLater(() -> setupButtonContainer());
    }

    protected abstract void setData(T data);

    protected abstract void setImage(Image image);

    private void setupImageClick() {
        ImageUtils.setCircleClip(image);
        image.setOnMouseClicked(_ -> modalUtils.showImageView(image.getImage(), parentStage));
    }

    private void setupRowClickEvents() {
        // Add Selection Style
        tableRow.setOnMouseClicked(_ -> {
            if (tableRow.getStyleClass().contains("selected")) {
                tableRow.getStyleClass().remove("selected");
            } else {
                tableRow.getStyleClass().add("selected");
            }
        });
        // Remove selection style
        tableRow.setOnMouseExited(_ -> {
            if (!tableRow.getStyleClass().contains("selected")) {
                tableRow.setStyle("");
            }
        });
    }

    protected abstract void setupButtonContainer();

    protected abstract void reloadTable();
}
