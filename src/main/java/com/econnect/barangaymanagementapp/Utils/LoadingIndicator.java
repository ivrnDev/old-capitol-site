package com.econnect.barangaymanagementapp.Utils;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public class LoadingIndicator {

    public static StackPane createLoadingIndicator(double width, double height) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(width);
        stackPane.setPrefHeight(height);

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefHeight(100);
        loadingIndicator.setPrefWidth(100);
        loadingIndicator.setVisible(true);
        stackPane.getChildren().add(loadingIndicator);

        return stackPane;
    }
}
