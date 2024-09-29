package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Enumeration.CustomizeModal;
import com.econnect.barangaymanagementapp.Enumeration.Modal;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.function.Consumer;

public class ModalUtils {
    private final Stage parentStage;
    private final SoundUtils soundUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private Stage currentStage = null;

    public ModalUtils(DependencyInjector dependencyInjector) {
        this.parentStage = dependencyInjector.getStage();
        this.soundUtils = dependencyInjector.getSoundUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    public void showModal(Modal modal, String header, String message) {
        showModal(modal, header, message, null);
    }

    public void showModal(Modal modal, String header, String message, Consumer<Boolean> callback) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(modal.getFxmlPath(), modal, header, message, callback);
            soundUtils.playSound(modal.getSound());
            Parent root = loader.load();
            Stage modalStage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            modalStage.initModality(callback == null ? Modality.NONE : Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.initOwner(currentStage != null ? currentStage : parentStage);
            modalStage.setScene(scene);

            setupFadeTransition(root);
            centerModal(modalStage);

            modalStage.showAndWait();
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }

    public void customizeModal(CustomizeModal customizeModal) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(customizeModal.getFxmlPath());
            Parent root = loader.load();
            Stage modalStage = new Stage();
            Scene scene = new Scene(root);
            currentStage = modalStage;
            scene.setFill(Color.TRANSPARENT);

            modalStage.initOwner(parentStage);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setScene(scene);

            setupFadeTransition(root);
            centerModal(modalStage);

            modalStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupFadeTransition(Parent root) {
        root.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void centerModal(Stage modalStage) {
        modalStage.setOnShown(event -> {
            double centerX = parentStage.getX() + parentStage.getWidth() / 2 - modalStage.getWidth() / 2;
            double centerY = parentStage.getY() + parentStage.getHeight() / 2 - modalStage.getHeight() / 2;
            modalStage.setX(centerX);
            modalStage.setY(centerY);
        });
    }


    public void closeModal() {
        if (currentStage != null) {
            currentStage.close();
            currentStage = null;
        }
    }


}
