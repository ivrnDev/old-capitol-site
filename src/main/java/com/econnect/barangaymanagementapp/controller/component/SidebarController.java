package com.econnect.barangaymanagementapp.controller.component;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.NavigationType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
import com.econnect.barangaymanagementapp.util.RolePermission;
import com.econnect.barangaymanagementapp.util.SceneManager;
import com.econnect.barangaymanagementapp.util.state.NavigationState;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Objects;

public class SidebarController {

    private final UserSession userSession;
    private final SceneManager sceneManager;
    private final NavigationState navigationState;
    private final ModalUtils modalUtils;
    private DepartmentType currentDepartment;
    private RoleType userRole;
    private LiveReloadUtils liveReloadUtils;

    @FXML
    private VBox navigationBar;

    public SidebarController(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getUserSession();
        this.sceneManager = dependencyInjector.getSceneManager();
        this.navigationState = dependencyInjector.getNavigationState();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
    }

    public void initialize() {
        if (userSession.hasSession()) {
            currentDepartment = userSession.getEmployeeDepartment();
            userRole = userSession.getEmployeeRole();
            loadNavigationBar();
        }
    }

    private void loadNavigationBar() {
        List<NavigationType> navigationItems = RolePermission.getNavigationByRole(currentDepartment, userRole);
        if (navigationState.getActiveItem() == null && !navigationItems.isEmpty()) {
            navigationState.setActiveItem(navigationItems.getFirst());
        }

        navigationBar.getChildren().clear();
        navigationItems.forEach(item -> {
            navigationBar.getChildren().add(createNavButton(item));
        });
    }

    private HBox createNavButton(NavigationType item) {
        HBox navButton = new HBox();
        ImageView navIcon = new ImageView();
        Text navText = new Text(item.getName());
        navButton.getStyleClass().add("nav-bar");
        navIcon.setFitWidth(35);
        navIcon.setFitHeight(35);

        if (item == navigationState.getActiveItem()) {
            navText.getStyleClass().add("selected-text");
            navButton.getStyleClass().add("active");
            navIcon.setImage(loadIcon(item.getIconPathSelected()));
        } else {
            navIcon.setImage(loadIcon(item.getIconPath()));
        }

        navButton.getChildren().addAll(navIcon, navText);
        navButton.setOnMouseClicked(_ -> {
            navigationState.setActiveItem(item);
            sceneManager.switchScene("view" + "/" + item.getLowerCaseName() + ".fxml");
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
                liveReloadUtils.stopListeningToUpdates();
                sceneManager.switchToDefaultScene();
            }
        });
    }
}
