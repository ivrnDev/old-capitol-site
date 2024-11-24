package com.econnect.barangaymanagementapp.controller.component;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HeaderController {
    @FXML
    private Text headerTitle, roleType;
    @FXML
    private ImageView profilePicture;

    private final ModalUtils modalUtils;
    private final Stage currentStage;
    private Image profilePictureImage;
    private final Employee loggedEmployee;

    public HeaderController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.currentStage = dependencyInjector.getStage();
        loggedEmployee = UserSession.getInstance().getCurrentEmployee();
    }

    public void initialize() {
        loadEmployeeDetails();
        populateEmployeeDetails();
        loadProfileImage();
        ImageUtils.setCircleClip(profilePicture);
    }

    private void loadEmployeeDetails() {
    }

    private void populateEmployeeDetails() {
        headerTitle.setText(loggedEmployee.getDepartment().getName());
        roleType.setText(loggedEmployee.getRole().getName());
    }

    private void loadProfileImage() {
        if (UserSession.getInstance().getEmployeeImage() != null) {
            profilePicture.setImage(UserSession.getInstance().getEmployeeImage());
            profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), currentStage));
            profilePicture.setCursor(Cursor.HAND);
        }
    }
}
