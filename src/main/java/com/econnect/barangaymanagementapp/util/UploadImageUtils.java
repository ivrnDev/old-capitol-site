package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.controller.component.SetupFileController;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

import static javafx.stage.Modality.WINDOW_MODAL;
import static javafx.stage.StageStyle.TRANSPARENT;

public class UploadImageUtils {
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final Stage rootStage;
    Runnable runnable;

    public UploadImageUtils(DependencyInjector dependencyInjector) {
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.rootStage = dependencyInjector.getStage();
    }

    public void initialize() {

    }

    public void loadSetupFile(Stage parentStage, Consumer<Image> callback) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(FXMLPath.SETUP_FILE.getFxmlPath());
            Parent root = loader.load();
            SetupFileController controller = loader.getController();
            controller.setCallback(callback);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initOwner(parentStage);
            stage.initStyle(TRANSPARENT);
            stage.initModality(WINDOW_MODAL);
            stage.setAlwaysOnTop(true);

            centerModal(stage);

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void centerModal(Stage stage) {
        stage.setOnShown(event -> {
            double centerX = rootStage.getX() + rootStage.getWidth() / 2 - stage.getWidth() / 2;
            double centerY = rootStage.getY() + rootStage.getHeight() / 2 - stage.getHeight() / 2;
            stage.setX(centerX);
            stage.setY(centerY);
        });
    }
}
