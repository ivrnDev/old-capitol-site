package com.econnect.barangaymanagementapp;

import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {

    private Popup popup;
    private ScheduledExecutorService scheduler;

    @Override
    public void start(Stage stage) {
        DependencyInjector dependencyInjector = new DependencyInjector(stage);
        stage.initStyle(StageStyle.DECORATED);
        SceneManager sceneManager = dependencyInjector.getSceneManager();
        sceneManager.switchToDefaultScene();
//        test(dependencyInjector);
//        checkResourcesInBackground(stage);
    }

    private void test(DependencyInjector dependencyInjector) {
        var repo = dependencyInjector.getResidentRepository();
        var emp = repo.findAllResidents();
        System.out.println(emp);

    }

    private void checkResourcesInBackground(Stage stage) {
        scheduler = Executors.newScheduledThreadPool(1);
        Runnable checkTask = () -> {
            boolean internetAvailable = isInternetAvailable();
            Platform.runLater(() -> {
                if (!internetAvailable) {
                    if (popup == null || !popup.isShowing()) {
                        showResourceDialog(stage);
                    }
                } else {
                    if (popup != null && popup.isShowing()) {
                        popup.hide();
                    }
                }
            });
        };

        scheduler.scheduleAtFixedRate(checkTask, 0, 10, TimeUnit.SECONDS);
    }

    private boolean isInternetAvailable() {
        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);  // 3 seconads timeout
            connection.connect();
            System.out.println("Connection response code: " + connection.getResponseCode());
            return connection.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private void showResourceDialog(Stage stage) {
//        if (!isInternetAvailable()) {
        popup = new Popup();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("View/Component/Modal/resource-error.fxml"));
        Parent root;
        try {
            root = loader.load();
            popup.getContent().add(root);
            popup.setAutoHide(false);


            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double popupWidth = screenBounds.getWidth();

            if (root instanceof HBox) {
                HBox hBoxRoot = (HBox) root;
                hBoxRoot.setPrefWidth(popupWidth);
                hBoxRoot.setMaxWidth(popupWidth);
            }
            popup.setX(0);
            popup.setY(screenBounds.getHeight() - screenBounds.getHeight());
            popup.setWidth(popupWidth);
            popup.setWidth(popupWidth);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }
    }

    public void shutdownScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }

    @Override
    public void stop() {
        shutdownScheduler();
    }

    public static void main(String[] args) {
        launch(args);
    }

}