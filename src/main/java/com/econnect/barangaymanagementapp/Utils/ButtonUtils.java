package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Enumeration.ButtonStyle;
import com.econnect.barangaymanagementapp.Interface.Callback;
import javafx.scene.control.Button;

public class ButtonUtils {
    public Button createButton(String text, ButtonStyle buttonStyle, Callback callback) {
        Button button = new Button(text);
        button.getStyleClass().add(buttonStyle.getRootStyle());
        button.setOnAction(event -> callback.onClicked());
        return button;
    }
}