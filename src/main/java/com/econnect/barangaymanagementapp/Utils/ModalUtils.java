package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.MainApplication;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class ModalUtils {
    private Stage modalStage = null;
    private final Stage parentStage;
    private final SoundUtils soundUtils;

    public ModalUtils(DependencyInjector dependencyInjector) {
        this.parentStage = dependencyInjector.getStage();
        this.soundUtils = dependencyInjector.getSoundUtils();
    }

    public void showModal(Modal modal, String header, String message) {
        showModal(modal, header, message, null); // Pass null as the callback
    }

    public void showModal(Modal modal, String header, String message, Consumer<Boolean> callback) {
        if (!isModalShowing()) {
            try {
                FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(modal.getFXMLPath()));
                loader.setControllerFactory(controllerClass -> {
                    try {
                        Constructor<?> constructor = controllerClass.getConstructor(Modal.class, String.class, String.class, Consumer.class);
                        return constructor.newInstance(modal, header, message, callback);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                             InvocationTargetException e) {
                        throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName(), e);
                    }
                });
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

            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private boolean isModalShowing() {
        return modalStage != null && modalStage.isShowing();
    }
}
