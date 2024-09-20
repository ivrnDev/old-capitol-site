package com.econnect.barangaymanagementapp.Controller.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class ModalController {

    @FXML
    private Button noBtn;

    @FXML
    private Button yesBtn;

    @FXML
    private Text messageText;

    @FXML
    private Text headerText;

    private Consumer<Boolean> callback;

    public void initialize() {
        yesBtn.setOnAction(_ -> handleCallBack(true));
        noBtn.setOnAction(_ -> handleCallBack(false));
    }

    public void setup(String header, String message, Consumer<Boolean> callback) {
        if (header == null || header.trim().isEmpty()) {
            headerText.setVisible(false);
        } else {
            headerText.setText(header);
        }
        messageText.setText(message);
        this.callback = callback;
    }

    private void handleCallBack(boolean result) {
        if (callback != null) {
            callback.accept(result);
        }
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) yesBtn.getScene().getWindow();
        stage.close();
    }
}
