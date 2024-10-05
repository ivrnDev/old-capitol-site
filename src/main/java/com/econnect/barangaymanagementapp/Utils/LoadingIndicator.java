package com.econnect.barangaymanagementapp.Utils;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public class LoadingIndicator {

    public static StackPane createLoadingIndicator(double width, double height) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(width);
        stackPane.setPrefHeight(height);
        stackPane.setStyle("-fx-background-color: rgba(255,255,255,0);");
        stackPane.setMouseTransparent(true);

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(true);
        stackPane.getChildren().add(loadingIndicator);

        return stackPane;
    }
}
