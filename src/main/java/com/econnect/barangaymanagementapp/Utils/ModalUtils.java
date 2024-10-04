package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Enumeration.CustomizeModal;
import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.Enumeration.ModalType;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
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

    private Stage customizeStage = null;
    private Stage modalStage = null;

    public ModalUtils(DependencyInjector dependencyInjector) {
        this.parentStage = dependencyInjector.getStage();
        this.soundUtils = dependencyInjector.getSoundUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
    }

    public void showModal(Modal modal, String header, String message) {
        showModal(modal, header, message, null);
    }

    public void showModal(Modal modal, String header, String message, Consumer<Boolean> callback) {
        if (modalStage != null) {
            return;
        }
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(modal.getFxmlPath(), modal, header, message, callback, this);
            Parent root = loader.load();
            root.setOpacity(0);
            modalStage = new Stage();
            Scene scene = new Scene(root);

            soundUtils.playSound(modal.getSound());
            scene.setFill(Color.TRANSPARENT);

            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.setX(-1000);
            modalStage.setY(-1000);
            modalStage.setScene(scene);

            if (modal.getModalType().equals(ModalType.NOTIFICATION)) {
                centerTop(modalStage);
                modalStage.initOwner(parentStage);
            }

            if (modal.getModalType().equals(ModalType.MODAL)) {
                setupFadeTransition(root);
                centerModal(modalStage);
                modalStage.initOwner(customizeStage != null ? customizeStage : parentStage);
            }
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.showAndWait();
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }


    public void customizeModal(CustomizeModal customizeModal) {
        if (customizeStage != null) {
            return;
        }

        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(customizeModal.getFxmlPath());
            Parent root = loader.load();
            root.setOpacity(0);
            customizeStage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            customizeStage.initOwner(parentStage);
            customizeStage.setX(-1000);
            customizeStage.setY(-1000);
            customizeStage.setScene(scene);

            customizeStage.initStyle(StageStyle.TRANSPARENT);
            customizeStage.initModality(Modality.WINDOW_MODAL);

            setupFadeTransition(root);
            centerModal(customizeStage);

            customizeStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void showImageView(Image image, Stage parent) {
        Stage resumeStage = new Stage();
        resumeStage.initModality(Modality.APPLICATION_MODAL);
        resumeStage.initOwner(parent);
        resumeStage.initStyle(StageStyle.TRANSPARENT);

        StackPane stackPane = new StackPane();

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        stackPane.setPrefWidth(screenWidth);
        stackPane.setPrefHeight(screenHeight);

        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(screenWidth * 0.6);
        imageView.setFitHeight(screenHeight * 0.8);

        stackPane.getChildren().add(imageView);

        Scene scene = new Scene(stackPane);
        scene.setFill(Color.TRANSPARENT);
        resumeStage.setScene(scene);

        stackPane.setOnMouseClicked(event -> resumeStage.close());

        resumeStage.show();
    }

    private void setupFadeTransition(Parent root) {
        Platform.runLater(() -> {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    private void centerModal(Stage modalStage) {
        modalStage.setOnShown(_ -> {
            double centerX = parentStage.getX() + parentStage.getWidth() / 2 - modalStage.getWidth() / 2;
            double centerY = parentStage.getY() + parentStage.getHeight() / 2 - modalStage.getHeight() / 2;
            modalStage.setX(centerX);
            modalStage.setY(centerY);
        });
    }

    private void centerTop(Stage modalStage) {
        modalStage.setOnShown(_ -> {
            double centerX = parentStage.getX() + parentStage.getWidth() / 2 - modalStage.getWidth() / 2;
            double topY = parentStage.getY() + 100;
            modalStage.setX(centerX);
            modalStage.setY(topY);
        });
    }

    public void closeModal() {
        if (modalStage != null) {
            modalStage.close();
            modalStage = null;
        }
    }

    public void closeCustomizeModal() {
        if (customizeStage != null) {
            customizeStage.close();
            customizeStage = null;
        }
    }


}
