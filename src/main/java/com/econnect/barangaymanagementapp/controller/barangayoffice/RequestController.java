package com.econnect.barangaymanagementapp.controller.barangayoffice;

import com.econnect.barangaymanagementapp.controller.barangayoffice.table.resident.ResidentApplicationTableController;
import com.econnect.barangaymanagementapp.controller.barangayoffice.table.resident.ResidentTableController;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.filter.ResidentRequestFilter;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.service.SearchService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.filter.ResidentRequestFilter.*;
import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.REQUEST_TABLE;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus.PENDING;
import static com.econnect.barangaymanagementapp.util.StatusUtils.INACTIVE_RESIDENT;

public class RequestController {
    @FXML
    private VBox contentPane;
    @FXML
    private VBox residentRequestContainer;
    @FXML
    private TextField residentRequestSearchField;
    @FXML
    private ComboBox<String> residentRequestComboBox;

    private final ResidentService residentService;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private ResidentTableController requestTableController;
    private ResidentApplicationTableController residentApplicationTableController;
    private final SearchService<Resident> searchService;
    private final LiveReloadUtils liveReloadUtils;
    private final DependencyInjector dependencyInjector;

    private List<Resident> allResidents;
    private StackPane loadingIndicator;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public RequestController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.residentService = dependencyInjector.getResidentService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.searchService = dependencyInjector.getResidentSearchService();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
    }

    public void initialize() {
        resetLiveReload();
        initializeSSEListener();

        setupListener();
        loadRequestTable();
        populateRows();
    }

    private void loadRequestTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(REQUEST_TABLE.getFxmlPath(), dependencyInjector);
            Parent requestTable = loader.load();
            requestTableController = loader.getController();
            residentRequestContainer.getChildren().add(requestTable);
        } catch (IOException e) {
            System.err.println("Error loading request table: " + e.getMessage());
        }
    }

    private void populateRows() {
        addTableLoadingIndicator();
        Runnable call = () -> {
            allResidents = residentService.findAllActiveResidents();
            Platform.runLater(() -> {
                removeTableLoadingIndicator();
                if (allResidents.isEmpty()) {
                    requestTableController.clearRow();
                    requestTableController.showNoData();
                } else {
                    updateResidentRow(allResidents);
                }
            });
        };

        Runnable onFailed = () -> {
            removeTableLoadingIndicator();
            System.err.println("Error loading residents");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void performSearch() {
        String searchText = residentRequestSearchField.getText().trim().toLowerCase();
        searchService.performSearch(
                searchText,
                allResidents,
                searchService.createResidentFilter(searchText),
                (filteredResidents) -> updateResidentRow(filteredResidents));
    }


    private void updateResidentRow(List<Resident> residents) {
        requestTableController.clearRow();

        if (residents.isEmpty()) {
            requestTableController.showNoData();
            return;
        }
        residents.forEach(resident -> requestTableController.addRow(resident));
    }

    public void updateResidentRow(String id) {
        Optional<Resident> updatedEmployee = residentService.findResidentById(id);
        updatedEmployee.ifPresentOrElse(resident -> {
            if (!INACTIVE_RESIDENT.contains(resident.getStatus())) {
                requestTableController.updateRow(resident);
            } else {
                requestTableController.deleteRow(resident.getId());

            }
        }, () -> requestTableController.deleteRow(id));
    }

    public void updateApplicationResidentRow(String id) {
        Optional<Resident> updatedEmployee = residentService.findResidentById(id);
        updatedEmployee.ifPresentOrElse(resident -> {
            if (resident.getStatus().equals(PENDING)) {
                residentApplicationTableController.updateRow(resident);
            } else {
                residentApplicationTableController.deleteRow(resident.getId());
            }
        }, () -> residentApplicationTableController.deleteRow(id));
    }


    private void setupListener() {
        residentRequestSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performSearch());
            searchDelay.playFromStart();
        });
        residentRequestComboBox.getItems().addAll(Arrays.stream(values()).map(ResidentRequestFilter::getName).toList());
        residentRequestComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
        });
    }

    //Live Reload
    private void initializeSSEListener() {
        residentService.listenToUpdates(result -> Platform.runLater(() -> {
            updateResidentRow(result);
            updateApplicationResidentRow(result);
        }));
    }

    private void resetLiveReload() {
        liveReloadUtils.stopListeningToUpdates();
    }

    //Loading Indicator
    public void addTableLoadingIndicator() {
        loadingIndicator = LoadingIndicator.createLoadingIndicator(residentRequestContainer.getWidth(), residentRequestContainer.getHeight());
        residentRequestContainer.getChildren().add(loadingIndicator);
    }

    public void removeTableLoadingIndicator() {
        residentRequestContainer.getChildren().remove(loadingIndicator);
    }

}