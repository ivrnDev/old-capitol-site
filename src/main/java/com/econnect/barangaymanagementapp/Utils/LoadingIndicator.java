package com.econnect.barangaymanagementapp.Utils;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoadingIndicator {
    public static StackPane createLoadingIndicator(double width, double height) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(width);
        stackPane.setPrefHeight(height);
        stackPane.setStyle("-fx-background-color: rgba(255,255,255,0);");
        stackPane.setMouseTransparent(true);
        stackPane.setVisible(true);

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(true);
        stackPane.getChildren().add(loadingIndicator);
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        return stackPane;
    }

    public static void executeWithLoadingIndicator(StackPane loadingIndicator, Runnable task, Runnable onFailure) {
        loadingIndicator.setVisible(true);

        Task<Void> backgroundTask = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    task.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                loadingIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                super.failed();
                loadingIndicator.setVisible(false);
                onFailure.run();
            }
        };

        new Thread(backgroundTask).start();
    }
}
