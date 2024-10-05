package com.econnect.barangaymanagementapp.Controller.HumanResources.Table;

import com.econnect.barangaymanagementapp.Enumeration.ButtonStyle;
import com.econnect.barangaymanagementapp.Utils.ButtonUtils;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.ImageUtils;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class EmployeeRowController {
    private final ModalUtils modalUtils;
    private final Stage parentStage;

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
    private HBox buttonContainer;

    public EmployeeRowController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.parentStage = dependencyInjector.getStage();
    }

    public void initialize() {
        setupProfileImageClick();
        setupRowClickEvents();
        setupButtonContainer();
    }

    private void setupProfileImageClick() {
        ImageUtils.setCircleClip(profilePicture);
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), parentStage));
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

    private void setupRowClickEvents() {
        tableRow.setOnMouseClicked(_ -> toggleRowSelection());
        tableRow.setOnMouseExited(_ -> resetRowStyleIfNotSelected());
    }

    private void toggleRowSelection() {
        if (tableRow.getStyleClass().contains("selected")) {
            tableRow.getStyleClass().remove("selected");
        } else {
            tableRow.getStyleClass().add("selected");
        }
    }

    private void resetRowStyleIfNotSelected() {
        if (!tableRow.getStyleClass().contains("selected")) {
            tableRow.setStyle("");
        }
    }

    private void setupButtonContainer() {
        Button updateBtn = ButtonUtils.createButton("Update", ButtonStyle.UPDATE, () -> {
            System.out.println("Clicked update");
        });
        Button viewBtn = ButtonUtils.createButton("View", ButtonStyle.VIEW, () -> {
            System.out.println("Clicked view");
        });
        Button deleteBtn = ButtonUtils.createButton("Delete", ButtonStyle.REJECT, () -> {
            System.out.println("Clicked delete");
        });

        buttonContainer.getChildren().addAll(updateBtn, viewBtn, deleteBtn);
    }
}