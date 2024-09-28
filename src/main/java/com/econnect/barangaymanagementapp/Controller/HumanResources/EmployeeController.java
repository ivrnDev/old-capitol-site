package com.econnect.barangaymanagementapp.Controller.HumanResources;

import com.econnect.barangaymanagementapp.Utils.ButtonUtils;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    @FXML
    private VBox content;

    private final ButtonUtils buttonUtils;


    public EmployeeController(DependencyInjector dependencyInjector) {
        this.buttonUtils = dependencyInjector.getButtonUtils();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


}