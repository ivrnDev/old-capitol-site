package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Enumeration.Modal;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.function.Consumer;

public class ModalUtils {
    private Stage modalStage = null;
    private final Stage parentStage;
    private final SoundUtils soundUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;

    public ModalUtils(DependencyInjector dependencyInjector) {
        this.parentStage = dependencyInjector.getStage();
        this.soundUtils = dependencyInjector.getSoundUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    public void showModal(Modal modal, String header, String message) {
        showModal(modal, header, message, null);
    }

    public void showModal(Modal modal, String header, String message, Consumer<Boolean> callback) {
        if (!isModalShowing()) {
            try {
                FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(modal.getFxmlPath(), modal, header, message, callback);
                soundUtils.playSound(modal.getSound());
                Parent root = loader.load();
                modalStage = new Stage();
                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                modalStage.initModality(callback == null ? Modality.NONE : Modality.APPLICATION_MODAL);
                modalStage.initStyle(StageStyle.TRANSPARENT);
                modalStage.initOwner(parentStage);
                modalStage.setScene(scene);
                modalStage.showAndWait();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private boolean isModalShowing() {
        return modalStage != null && modalStage.isShowing();
    }
}
