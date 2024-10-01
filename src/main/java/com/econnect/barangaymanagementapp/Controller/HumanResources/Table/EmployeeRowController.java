package com.econnect.barangaymanagementapp.Controller.HumanResources.Table;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

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
    private HBox tableRow;

    @FXML
    public void initialize() {
        tableRow.setOnMouseClicked(event -> {
            if (tableRow.getStyleClass().contains("selected")) {
                tableRow.getStyleClass().remove("selected");
            } else {
                tableRow.getStyleClass().add("selected");
            }
        });

//        tableRow.setOnMouseEntered(event -> {
//            tableRow.setStyle("-fx-background-color: rgba(173, 216, 230, 0.3);"); // Light blue on hover
//        });

        tableRow.setOnMouseExited(event -> {
            if (!tableRow.getStyleClass().contains("selected")) {
                tableRow.setStyle(""); // Reset to default
            }
        });
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