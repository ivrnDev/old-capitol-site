package com.econnect.barangaymanagementapp.controller.barangayoffice;

import com.econnect.barangaymanagementapp.controller.barangayoffice.table.resident.ResidentTableController;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.service.SearchService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.RESIDENT_TABLE;

public class ResidentController {

    @FXML
    private VBox contentPane;

    @FXML
    public Button addResidentBtn;

    @FXML
    private TextField searchField;

    private final ResidentService residentService;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private ResidentTableController residentTableController;
    private final SearchService<Resident> searchService;
    private final DependencyInjector dependencyInjector;

    private List<Resident> allResidents;
    private StackPane loadingIndicator;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public ResidentController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.residentService = dependencyInjector.getResidentService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.searchService = dependencyInjector.getResidentSearchService();
    }

    public void initialize() {
        addResidentBtn.setOnMouseClicked(_ -> showAddResident());
        loadResidentTable();
        populateResidentRows();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performSearch());
            searchDelay.playFromStart();
        });
    }

    private void loadResidentTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(RESIDENT_TABLE.getFxmlPath(), dependencyInjector, this);
            Parent residentTable = loader.load();
            residentTableController = loader.getController();
            contentPane.getChildren().add(residentTable);
        } catch (IOException e) {
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    public void populateResidentRows() {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(contentPane.getWidth(), contentPane.getHeight());
        Platform.runLater(() -> contentPane.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            allResidents = residentService.findAllNonDeletedResidents();
            Platform.runLater(() -> {
                contentPane.getChildren().remove(loadingIndicator);
                if (allResidents.isEmpty()) {
                    residentTableController.clearRow();
                    residentTableController.showNoData();
                } else {
                    updateResidentRow(allResidents);
                }
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> contentPane.getChildren().remove(loadingIndicator));
            System.err.println("Error loading employees");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        searchService.performSearch(
                searchText,
                allResidents,
                searchService.createResidentFilter(searchText),
                (filteredResidents) -> updateResidentRow(filteredResidents));
    }

    public void addLoadingIndicator() {
        loadingIndicator = LoadingIndicator.createLoadingIndicator(contentPane.getWidth(), contentPane.getHeight());
        contentPane.getChildren().add(loadingIndicator);
    }

    public void removeLoadingIndicator() {
        contentPane.getChildren().remove(loadingIndicator);
    }

    public void reloadTable() {
        populateResidentRows();
    }

    private void updateResidentRow(List<Resident> residents) {
        residentTableController.clearRow();

        if (residents.isEmpty()) {
            residentTableController.showNoData();
            return;
        }
        residents.forEach(resident -> residentTableController.addRow(resident));
    }

    private void showAddResident() {
        modalUtils.customizeModal(FXMLPath.ADD_RESIDENT);
    }
}