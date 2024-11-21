package com.econnect.barangaymanagementapp.controller.component;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HeaderController {
    @FXML
    private Text headerTitle, roleType;
    @FXML
    private ImageView profilePicture;
    @FXML
    private HBox profileContainer;

    private final ModalUtils modalUtils;
    private final ImageService imageService;
    private final Stage currentStage;
    private Image profilePictureImage;
    private final Employee loggedEmployee;

    public HeaderController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.imageService = dependencyInjector.getImageService();
        this.currentStage = dependencyInjector.getStage();
        loggedEmployee = UserSession.getInstance().getCurrentEmployee();
    }

    public void initialize() {
        loadEmployeeDetails();
        populateEmployeeDetails();
        loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), loggedEmployee.getProfileUrl());
        ImageUtils.setCircleClip(profilePicture);
    }

    private void loadEmployeeDetails() {
    }

    private void populateEmployeeDetails() {
        headerTitle.setText(loggedEmployee.getDepartment().getName());
        roleType.setText(loggedEmployee.getRole().getName());
    }

    private void loadProfileImage(String directory, String link) {
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), currentStage));
        profilePicture.setCursor(Cursor.HAND);
        profilePicture.setVisible(false);
        profilePicture.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(profileContainer.getWidth(), profileContainer.getHeight());
        Platform.runLater(() -> profileContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            profilePictureImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                profileContainer.getChildren().remove(loadingIndicator);
                profilePicture.setImage(profilePictureImage);
                profilePicture.setVisible(true);
                profilePicture.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> profileContainer.getChildren().remove(loadingIndicator));
            profilePicture.setVisible(true);
            profilePicture.setManaged(true);
            System.err.println("Error loading employees");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }
}
