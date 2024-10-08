package com.econnect.barangaymanagementapp.controller.component;

import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.NavigationType;
import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.util.*;
import com.econnect.barangaymanagementapp.util.state.NavigationState;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;

public class SidebarController {

    private final UserSession userSession;
    private final SceneManager sceneManager;
    private final NavigationState navigationState;
    private final ModalUtils modalUtils;
    private DepartmentType currentDepartment;

    @FXML
    private VBox navigationBar;

    public SidebarController(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getUserSession();
        this.sceneManager = dependencyInjector.getSceneManager();
        this.navigationState = dependencyInjector.getNavigationState();
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    public void initialize() {
        if (userSession.hasSession()) {
            currentDepartment = userSession.getEmployeeDepartment();
            loadNavigationBar();
        }
    }

    private void loadNavigationBar() {
        if (navigationState.getActiveItem() == null && !currentDepartment.getNavigationItems().isEmpty()) {
            navigationState.setActiveItem(currentDepartment.getNavigationItems().getFirst());
        }

        navigationBar.getChildren().clear();
        currentDepartment.getNavigationItems().forEach(item -> {
            navigationBar.getChildren().add(createNavButton(item));
        });
    }

    private HBox createNavButton(NavigationType item) {
        HBox navButton = new HBox();
        ImageView navIcon = new ImageView();
        Text navText = new Text(item.getName());
        navButton.getStyleClass().add("nav-bar");
        navIcon.setFitWidth(50);
        navIcon.setFitHeight(50);

        if (item == navigationState.getActiveItem()) {
            navButton.getStyleClass().add("active");
            navIcon.setImage(loadIcon(item.getIconPathSelected()));
        } else {
            navIcon.setImage(loadIcon(item.getIconPath()));
        }

        navButton.getChildren().addAll(navIcon, navText);
        navButton.setOnMouseClicked(_ -> {
            navigationState.setActiveItem(item);
            sceneManager.switchScene("View/" + currentDepartment.getDirectoryName() + "/" + item.getLowerCaseName() + ".fxml");
        });
        return navButton;
    }

    private Image loadIcon(String iconPath) {
        try {
            return new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(iconPath)));
        } catch (NullPointerException e) {
            System.err.println("Icon resource not found: " + iconPath);
            return new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("Icon/default-icon.png")));
        }
    }

    @FXML
    public void logout() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Logout", "Are you sure you want to logout?", isConfirmed -> {
            if (isConfirmed) {
                navigationState.setActiveItem(null);
                userSession.clearSession();
                sceneManager.switchToDefaultScene();
            }
        });
    }
}
