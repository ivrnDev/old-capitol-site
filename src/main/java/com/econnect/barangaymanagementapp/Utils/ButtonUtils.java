package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Enumeration.ButtonStyle;
import com.econnect.barangaymanagementapp.Interface.Callback;
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
