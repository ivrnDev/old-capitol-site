package com.econnect.barangaymanagementapp.Controller.HumanResources.Table.Application;

import com.econnect.barangaymanagementapp.Enumeration.ButtonStyle;
import com.econnect.barangaymanagementapp.Enumeration.CustomizeModal;
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

public class ApplicationRowController {
    private final ModalUtils modalUtils;
    private final Stage parentStage;

    @FXML
    private HBox tableRow;

    @FXML
    private ImageView profilePicture;

    @FXML
    private Label residentIdLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private HBox buttonContainer;

    public ApplicationRowController(DependencyInjector dependencyInjector) {
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

    public void setEmployeeData(String employeeId, String lastName, String firstName, String status, String date, String time, Image profileImage) {
        residentIdLabel.setText(employeeId);
        lastNameLabel.setText(lastName);
        firstNameLabel.setText(firstName);
        statusLabel.setText(status);
        dateLabel.setText(date);
        timeLabel.setText(time);
        profilePicture.setImage(profileImage);
    }

    public void setProfileImage(Image profileImage) {
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
        Button viewBtn = ButtonUtils.createButton("View", ButtonStyle.VIEW, () -> {
            modalUtils.customizeModal(CustomizeModal.VIEW_EMPLOYEE);

        });
        Button acceptBtn = ButtonUtils.createButton("Accept", ButtonStyle.ACCEPT, () -> {
            System.out.println("Clicked accept");

        });
        Button rejectBtn = ButtonUtils.createButton("Reject", ButtonStyle.REJECT, () -> {
            System.out.println("Clicked delete");
        });

        buttonContainer.getChildren().addAll(viewBtn, acceptBtn, rejectBtn);
    }
}