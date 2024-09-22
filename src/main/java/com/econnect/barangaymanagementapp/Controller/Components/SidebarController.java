package com.econnect.barangaymanagementapp.Controller.Components;

import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.NavigationItems;
import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.NavigationState;
import com.econnect.barangaymanagementapp.Utils.SceneManager;
import com.econnect.barangaymanagementapp.Utils.UserSession;
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
    private Departments currentDepartment;

    @FXML
    private VBox navigationBar;

    public SidebarController(DependencyInjector dependencyInjector) {
        this.userSession = dependencyInjector.getUserSession();
        this.sceneManager = dependencyInjector.getSceneManager();
        this.navigationState = dependencyInjector.getNavigationState();
    }

    public void initialize() {
        if (userSession != null && userSession.getCurrentEmployee() != null) {
            currentDepartment = userSession.getCurrentEmployee().getDepartment();
            if (navigationState.getActiveItem() == null && !currentDepartment.getNavigationItems().isEmpty()) {
                navigationState.setActiveItem(currentDepartment.getNavigationItems().getFirst());
            }
            loadNavigationBar();
        }
    }

    private HBox createNavButton(NavigationItems item) {
        HBox navButton = new HBox();
        ImageView navIcon = new ImageView();
        Text navText = new Text(item.getName());

        navIcon.setImage(loadIcon(item));
        navButton.getStyleClass().add("nav-bar");

        if (item == navigationState.getActiveItem()) {
            navButton.getStyleClass().add("active");
        }

        navButton.getChildren().addAll(navIcon, navText);
        navButton.setOnMouseClicked(_ -> {
            navigationState.setActiveItem(item);
            sceneManager.switchScene("View/" + currentDepartment.getDirectoryName() + "/" + item.getLowerCaseName() + ".fxml");
            loadNavigationBar();
        });
        return navButton;
    }

    private void loadNavigationBar() {
        navigationBar.getChildren().clear();
        currentDepartment.getNavigationItems().forEach(item -> {
            navigationBar.getChildren().add(createNavButton(item));
        });
    }

    private Image loadIcon(NavigationItems item) {
        try {
            return new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("Icons/" + item.getLowerCaseName() + ".png")));
        } catch (NullPointerException e) {
            return new Image(MainApplication.class.getResourceAsStream("Icons/default-icon.png"));
        }
    }

    @FXML
    public void logout() {
        userSession.clearSession();
        sceneManager.switchToDefaultScene();
    }
}
