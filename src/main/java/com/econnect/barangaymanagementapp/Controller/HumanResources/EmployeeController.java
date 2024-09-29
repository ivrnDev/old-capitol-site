package com.econnect.barangaymanagementapp.Controller.HumanResources;

import com.econnect.barangaymanagementapp.Enumeration.CustomizeModal;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Utils.ButtonUtils;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    @FXML
    private VBox content;

    @FXML
    private Button addEmployeeBtn;

    private final ButtonUtils buttonUtils;
    private final EmployeeService employeeService;
    private final ModalUtils modalUtils;


    public EmployeeController(DependencyInjector dependencyInjector) {
        this.buttonUtils = dependencyInjector.getButtonUtils();
        this.employeeService = dependencyInjector.getEmployeeService();
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addEmployeeBtn.setOnAction(_ -> {
            modalUtils.customizeModal(CustomizeModal.ADD_EMPLOYEE);
        });

    }
}