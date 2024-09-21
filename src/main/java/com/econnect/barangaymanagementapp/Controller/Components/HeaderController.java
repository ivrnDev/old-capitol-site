package com.econnect.barangaymanagementapp.Controller.Components;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class HeaderController {
    @FXML
    private Text headerTitle;

    @FXML
    private Text greetingText;

    public void setHeaderTitle(String title) {
        headerTitle.setText(title);
    }

    public void setGreetingText(String greeting) { greetingText.setText(greeting); }
}
