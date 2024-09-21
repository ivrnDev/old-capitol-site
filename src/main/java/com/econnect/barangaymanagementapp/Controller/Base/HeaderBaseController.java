package com.econnect.barangaymanagementapp.Controller.Base;

import com.econnect.barangaymanagementapp.Controller.Components.HeaderController;
import javafx.fxml.FXML;

public abstract class HeaderBaseController {
    @FXML
    private HeaderController headerController;

    protected void setHeader(String title, String greeting) {
        headerController.setHeaderTitle(title);
        headerController.setGreetingText(greeting);
    }
}
