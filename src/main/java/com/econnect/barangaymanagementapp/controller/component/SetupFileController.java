package com.econnect.barangaymanagementapp.controller.component;

import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.WebCam;
import com.econnect.barangaymanagementapp.util.ui.FileChooserUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.function.Consumer;

public class SetupFileController {
    @FXML
    private AnchorPane rootContainer;
    @FXML
    private ImageView closeBtn;
    @FXML
    private VBox uploadImageBtn, takePhotoBtn;

    private final WebCam webCam;
    private final FileChooserUtils fileChooserUtils;
    private Stage currentStage;
    private Consumer<Image> callback;

    public SetupFileController(DependencyInjector dependencyInjector) {
        this.fileChooserUtils = dependencyInjector.getFileChooserUtils();
        this.webCam = dependencyInjector.getWebCam();
        Platform.runLater(() -> currentStage = (Stage) rootContainer.getScene().getWindow());
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeWindow());
        uploadImageBtn.setOnMouseClicked(_ -> openFileChooser());
        takePhotoBtn.setOnMouseClicked(_ -> openWebCam());
    }

    private void openWebCam() {
        webCam.startWithCapture(currentStage);
        webCam.setOnCaptureImage(image -> {
            callback.accept(image);
            closeWindow();
        });
    }

    private void openFileChooser() {
        FileChooser fileChooser = fileChooserUtils.createFileChooser();
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            try {
                callback.accept(new Image(new FileInputStream(file)));
                closeWindow();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void closeWindow() {
        currentStage.close();
    }

    public void setCallback(Consumer<Image> callback) {
        this.callback = callback;
    }

}

