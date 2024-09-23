package com.econnect.barangaymanagementapp.Controller.Component.Modal;

import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.Enumeration.ModalType;
import com.econnect.barangaymanagementapp.MainApplication;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.function.Consumer;

public class ModalController {

    @FXML
    private VBox rootPane;

    @FXML
    private Text headerText;

    @FXML
    private Text messageText;

    @FXML
    private Button acceptBtn;

    @FXML
    private Button rejectBtn;

    @FXML
    private ImageView icon;

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
        rootPane.getStyleClass().add(modal.getRootStyle());

        loadIcon();
        setTextColor();
        setButtonStyle();
    }

    private void loadIcon() {
        if (modal.getIcon() != null) {
            InputStream iconStream = MainApplication.class.getResourceAsStream(modal.getIcon());
            if (iconStream != null) {
                icon.setImage(new Image(iconStream));
            } else {
                System.err.println("Icon resource not found: " + modal.getIcon());
            }
        }
    }

    private void setTextColor() {
        if (modal.getTextColor() != null) {
            headerText.setFill(javafx.scene.paint.Color.web(modal.getTextColor()));
        }
    }

    private void setButtonStyle() {
        if (modal.getButtonStyle() != null) {
            acceptBtn.getStyleClass().add(modal.getButtonStyle());
            if ("default-button".equals(modal.getButtonStyle())) {
                acceptBtn.setTextFill(javafx.scene.paint.Color.web("#000000"));
            }
        }
    }

    private void initializeModalActions() {
        if (modal.getModalType() == ModalType.MODAL) {
            acceptBtn.setOnAction(_ -> handleCallBack(true));
            rejectBtn.setOnAction(_ -> handleCallBack(false));
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
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), rootPane);
        fadeTransition.setByValue(1.0);
        fadeTransition.setDelay(Duration.seconds(1.5));
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(_ -> stage.close());
        fadeTransition.play();
    }

    private void closeWindow() {
        Stage stage = (Stage) acceptBtn.getScene().getWindow();
        stage.close();
    }
}
