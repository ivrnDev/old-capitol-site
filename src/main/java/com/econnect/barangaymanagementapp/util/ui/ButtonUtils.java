package com.econnect.barangaymanagementapp.util.ui;

import com.econnect.barangaymanagementapp.enumeration.ui.ButtonStyle;
import com.econnect.barangaymanagementapp.interfaces.Callback;
import javafx.scene.Cursor;
import javafx.scene.control.Button;

public class ButtonUtils {
    public static Button createButton(String text, ButtonStyle buttonStyle, Callback callback) {
        Button button = new Button(text);
        button.setPrefWidth(70);
        button.setPrefHeight(25);
        button.setCursor(Cursor.HAND);
        button.getStyleClass().add("button");
        button.getStyleClass().add(buttonStyle.getRootStyle());
        button.setOnAction(_ -> callback.onClicked());
        return button;
    }
}
