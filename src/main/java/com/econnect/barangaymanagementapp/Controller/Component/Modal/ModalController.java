package com.econnect.barangaymanagementapp.Controller.Component.Modal;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.function.Consumer;

public class ModalController {

    @FXML
    private VBox rootPane;

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
        initializeModal();
        initializeModalActions();
    }

    private void initializeModal() {
        headerText.setText(header);
        messageText.setText(message);
    }

    private void initializeModalActions() {
        if (acceptBtn != null && rejectBtn != null) {
            acceptBtn.setOnAction(event -> handleCallBack(true));
            rejectBtn.setOnAction(event -> handleCallBack(false));
            return;
        }
        Platform.runLater(this::fadeOutAndClose);
    }

    private void handleCallBack(boolean result) {
        if (callback != null) {
            callback.accept(result);
        }
        closeWindow();
    }

    private void fadeOutAndClose() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(5), rootPane);
        fadeTransition.setByValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(_ -> stage.close());
        fadeTransition.play();
    }

    private void closeWindow() {
        Stage stage = (Stage) acceptBtn.getScene().getWindow();
        stage.close();
    }
}
