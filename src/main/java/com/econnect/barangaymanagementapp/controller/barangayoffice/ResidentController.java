package com.econnect.barangaymanagementapp.controller.barangayoffice;

import com.econnect.barangaymanagementapp.controller.barangayoffice.table.resident.ResidentApplicationTableController;
import com.econnect.barangaymanagementapp.controller.barangayoffice.table.resident.ResidentTableController;
import com.econnect.barangaymanagementapp.controller.shared.base.BaseTableController;
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

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.RESIDENT_APPLICATION_TABLE;
import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.RESIDENT_TABLE;

public class ResidentController {
    @FXML
    private TextField residentApplicationSearchField, residentListSearchField;

    @FXML
    private VBox residentApplicationContent, residentListContent;

    @FXML
    private Button addResidentBtn;

    private final ResidentService residentService;
    private final SearchService<Resident> searchService;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;
    private final ModalUtils modalUtils;
    private ResidentApplicationTableController residentApplicationTableController;
    private ResidentTableController residentTableController;
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private StackPane loadingIndicator;

    public ResidentController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.residentService = dependencyInjector.getResidentService();
        this.searchService = dependencyInjector.getResidentSearchService();
    }

    public void initialize() {
        addResidentBtn.setOnMouseClicked(_ -> modalUtils.customizeModal(FXMLPath.ADD_RESIDENT));
        residentApplicationTableController = loadTable(RESIDENT_APPLICATION_TABLE);
        residentTableController = loadTable(RESIDENT_TABLE);
//        populateResidentApplicationRow();
//        populateResidentRow();
        residentApplicationSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performSearch());
            searchDelay.playFromStart();
        });
    }

    private <T extends BaseTableController<Resident>> T loadTable(FXMLPath fxmlPath) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(fxmlPath.getFxmlPath(), dependencyInjector, this);
            Parent residentApplicationTable = loader.load();
            T tableController = loader.getController();
            if (fxmlPath.equals(RESIDENT_APPLICATION_TABLE)) {
                residentApplicationContent.getChildren().add(residentApplicationTable);
            } else {
                residentListContent.getChildren().add(residentApplicationTable);
            }
            return tableController;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading employee table: " + e.getMessage());
            return null;
        }
    }

    private void populateResidentApplicationRow() {
        addLoadingIndicator();
        Runnable call = () -> {
            List<Resident> allResidentApplications = residentService.findAllPendingResidents();

            Platform.runLater(() -> {
                removeLoadingIndicator();
                residentApplicationTableController.clearRow();
                if (allResidentApplications.isEmpty()) {
                    residentApplicationTableController.showNoData();
                    return;
                }
                if (allResidentApplications.isEmpty()) {
                    residentApplicationTableController.showNoData();
                    return;
                }
                allResidentApplications.forEach(resident -> residentApplicationTableController.addRow(resident));
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> residentApplicationContent.getChildren().remove(loadingIndicator));
            System.err.println("Error loading employees");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void populateResidentRow() {
        addLoadingIndicator();
        Runnable call = () -> {
            List<Resident> allResidents = residentService.findAllResidents();

            Platform.runLater(() -> {
                removeLoadingIndicator();
                residentApplicationTableController.clearRow();
                if (allResidents.isEmpty()) {
                    residentApplicationTableController.showNoData();
                    return;
                }
                if (allResidents.isEmpty()) {
                    residentApplicationTableController.showNoData();
                    return;
                }
                allResidents.forEach(resident -> residentTableController.addRow(resident));
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> residentListContent.getChildren().remove(loadingIndicator));
            System.err.println("Error loading employees");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void performSearch() {
//        String searchText = residentApplicationSearchField.getText().trim().toLowerCase();
//        searchService.performSearch(
//                searchText,
//                allResidentApplications,
//                searchService.createEmployeeApplicationFilter(searchText),
//                (filteredApplications) -> updateResidentApplication(filteredApplications));
    }

    public void addLoadingIndicator() {
        loadingIndicator = LoadingIndicator.createLoadingIndicator(residentApplicationContent.getWidth(), residentApplicationContent.getHeight());
        residentApplicationContent.getChildren().add(loadingIndicator);
    }

    public void removeLoadingIndicator() {
        residentApplicationContent.getChildren().remove(loadingIndicator);
    }

    public void reloadTable() {
        populateResidentApplicationRow();
    }
}
