package com.econnect.barangaymanagementapp.Controller.HumanResources.Table;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EmployeeRowController {

    @FXML
    private Label employeeIdLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label positionLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView profilePicture;

    @FXML
    public void initialize() {

    }

    public void setEmployeeData(String employeeId, String lastName, String firstName, String position, String department, String status, Image profileImage) {
        employeeIdLabel.setText(employeeId);
        lastNameLabel.setText(lastName);
        firstNameLabel.setText(firstName);
        positionLabel.setText(position);
        departmentLabel.setText(department);
        statusLabel.setText(status);
        profilePicture.setImage(profileImage);
    }


}