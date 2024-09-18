package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Controller.ModalController;
import com.econnect.barangaymanagementapp.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ModalUtils {
    private static Stage modalStage = null;

    public static void showConfirmationModal(String header, String message, ModalCallback callback) {
        //Prevent modal duplication
        if (isModalShowing()) return;

        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("View/Components/modal.fxml"));
            Parent root = loader.load();
            ModalController controller = loader.getController();
            controller.setup(header, message, (isConfirmed) -> callback.onResult(isConfirmed));

            modalStage = new Stage();
            Scene scene = new Scene(root);

            modalStage.initStyle(StageStyle.UNDECORATED);
            modalStage.setScene(scene);
            modalStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public interface ModalCallback {
        void onResult(boolean isConfirmed);
    }

    private static boolean isModalShowing() {
        return modalStage != null && modalStage.isShowing();
    }
}
