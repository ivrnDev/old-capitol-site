package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class ModalUtils {
    private Stage modalStage = null;
    private final Stage parentStage;

    public ModalUtils(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void showModal(Modal modal, String header, String message, Consumer<Boolean> callback) {
        if (!isModalShowing()) {
            try {
                FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(modal.getFXMLPath()));
                loader.setControllerFactory(controllerClass -> {
                    try {
                        Constructor<?> constructor = controllerClass.getConstructor(String.class, String.class, Consumer.class);
                        return constructor.newInstance(header, message, callback);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                             InvocationTargetException e) {
                        throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName(), e);
                    }
                });
                Parent root = loader.load();
                modalStage = new Stage();
                Scene scene = new Scene(root);

                modalStage.initStyle(StageStyle.UNDECORATED);
                modalStage.initOwner(parentStage);
                modalStage.initModality(Modality.WINDOW_MODAL);
                modalStage.setScene(scene);
                modalStage.showAndWait();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private boolean isModalShowing() {
        return modalStage != null && modalStage.isShowing();
    }
}
