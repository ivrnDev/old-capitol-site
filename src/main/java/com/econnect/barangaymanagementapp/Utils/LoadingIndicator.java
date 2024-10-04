package com.econnect.barangaymanagementapp.Utils;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public class LoadingIndicator {

    public static StackPane createLoadingIndicator(double width, double height) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(width);
        stackPane.setPrefHeight(height);

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(true);
        stackPane.getChildren().add(loadingIndicator);

        return stackPane;
    }

    public static void hideLoadingIndicator(StackPane loadingIndicatorContainer) {
        loadingIndicatorContainer.setVisible(false);
    }
}
