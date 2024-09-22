package com.econnect.barangaymanagementapp.Controller.Component.Modal;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class ModalController {

    @FXML
    private Button acceptBtn;

    @FXML
    private Button rejectBtn;

    @FXML
    private Text messageText;

    @FXML
    private Text headerText;

    private final String header;
    private final String message;
    private final Consumer<Boolean> callback;

    public ModalController(String header, String message, Consumer<Boolean> callback) {
        this.header = header;
        this.message = message;
        this.callback = callback;
    }

    public void initialize() {
        if (header == null || header.trim().isEmpty()) {
            headerText.setVisible(false);
            headerText.setManaged(false);
        } else {
            headerText.setText(header);
        }

        messageText.setText(message);

        if (callback != null) {
            acceptBtn.setVisible(true);
            rejectBtn.setVisible(true);
            acceptBtn.setOnAction(event -> handleCallBack(true));
            rejectBtn.setOnAction(event -> handleCallBack(false));
        } else {
            acceptBtn.setVisible(false);
            acceptBtn.setManaged(false);
            rejectBtn.setManaged(false);
            rejectBtn.setVisible(false);
        }
    }


//    public void setup(String header, String message, Consumer<Boolean> callback) {
//        if (header == null || header.trim().isEmpty()) {
//            headerText.setVisible(false);
//            headerText.setManaged(false);
//        } else {
//            headerText.setText(header);
//        }
//        messageText.setText(message);
//        this.callback = callback;
//    }

    private void handleCallBack(boolean result) {
        if (callback != null) {
            callback.accept(result);
        }
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) acceptBtn.getScene().getWindow();
        stage.close();
    }
}
