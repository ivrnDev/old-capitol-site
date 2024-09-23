package com.econnect.barangaymanagementapp.Controller.Component.Modal;

import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.Enumeration.ModalType;
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

    private final Modal modal;
    private final String header;
    private final String message;
    private final Consumer<Boolean> callback;

    public ModalController(Modal modal, String header, String message, Consumer<Boolean> callback) {
        this.modal = modal;
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
        if (modal.getModalType() == ModalType.NOTIFICATION) {
            switchNotificationDesign();

        }
        switchModalDesign();
    }

    private void switchNotificationDesign() {
        switch (modal.name()) {
            case "SUCCESS":
                setModalDesign("success-style", "#026917");
                break;
            case "WARNING":
                setModalDesign("warning-style", "#9e9600");
                break;
            case "ERROR":
                setModalDesign("error-style", "#b30707");
                break;
            default:
                break;
        }
    }

    private void switchModalDesign() {
        switch (modal.name()) {
            case "CLASSIC":
                setModalDesign("default-style", "#000000");
                break;
            case "DEFAULT":
                setModalDesign("default-style", "#000000", "default-button");
                break;
            case "DEFAULT_APPROVE":
                setModalDesign("default-style", "#000000", "approve-button");
                break;
            case "DEFAULT_REJECT":
                setModalDesign("default-style", "#000000", "reject-button");
                break;
            default:
                break;
        }

    }

    private void setModalDesign(String rootStyle, String textColor) {
        rootPane.getStyleClass().add(rootStyle);
        headerText.setFill(javafx.scene.paint.Color.web(textColor));
    }

    private void setModalDesign(String rootStyle, String textColor, String buttonStyle) {
        rootPane.getStyleClass().add(rootStyle);
        headerText.setFill(javafx.scene.paint.Color.web(textColor));

        if (modal.name().equals("DEFAULT")) {
            acceptBtn.setTextFill(javafx.scene.paint.Color.web(textColor));
        }
        acceptBtn.getStyleClass().add(buttonStyle);

    }


    private void initializeModalActions() {
        if (modal.getModalType() == ModalType.MODAL) {
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
