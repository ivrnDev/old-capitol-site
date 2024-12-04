package com.econnect.barangaymanagementapp.controller;

import com.econnect.barangaymanagementapp.controller.table.resident.ResidentApplicationTableController;
import com.econnect.barangaymanagementapp.controller.table.resident.ResidentTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.service.SearchService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
import com.econnect.barangaymanagementapp.util.RolePermission;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.RESIDENT_APPLICATION_TABLE;
import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.RESIDENT_TABLE;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus.PENDING;
import static com.econnect.barangaymanagementapp.util.StatusUtils.INACTIVE_RESIDENT;

public class ResidentController {
    @FXML
    private VBox contentPane;
    @FXML
    private VBox residentApplicationContent, residentListContent;
    @FXML
    private TextField residentApplicationSearchField, residentListSearchField;
    @FXML
    public Button addResidentBtn;
    @FXML
    private AnchorPane applicationHeader;


    private final ResidentService residentService;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private ResidentTableController residentTableController;
    private ResidentApplicationTableController residentApplicationTableController;
    private final SearchService<Resident> searchService;
    private final LiveReloadUtils liveReloadUtils;
    private final DependencyInjector dependencyInjector;
    private final Employee loggedEmployee;

    private List<Resident> allResidents;
    private List<Resident> allPendingResidents;
    private StackPane loadingIndicator;
    private boolean showApplication = true;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public ResidentController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.residentService = dependencyInjector.getResidentService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.searchService = dependencyInjector.getResidentSearchService();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
        this.loggedEmployee = UserSession.getInstance().getCurrentEmployee();
    }

    public void initialize() {
        resetLiveReload();
        triggerPermission();
        loadResidentTable();
        loadResidentApplicationTable();
        populateResidentRows();
        populateResidentApplicationRows();
        setupListener();
        initializeSSEListener();
    }

    private void loadResidentTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(RESIDENT_TABLE.getFxmlPath(), dependencyInjector);
            Parent residentTable = loader.load();
            residentTableController = loader.getController();
            if (showApplication) {
                residentListContent.getChildren().add(residentTable);
            } else {
                contentPane.getChildren().add(residentTable);
            }
        } catch (IOException e) {
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    private void loadResidentApplicationTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(RESIDENT_APPLICATION_TABLE.getFxmlPath(), dependencyInjector);
            Parent residentTable = loader.load();
            residentApplicationTableController = loader.getController();
            residentApplicationContent.getChildren().add(residentTable);
        } catch (IOException e) {
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    public void populateResidentRows() {
        addResidentLoadingIndicator();
        Runnable call = () -> {
            allResidents = residentService.findAllActiveResidents();
            Platform.runLater(() -> {
                removeResidentLoadingIndicator();
                if (allResidents.isEmpty()) {
                    residentTableController.clearRow();
                    residentTableController.showNoData();
                } else {
                    updateResidentRow(allResidents);
                }
            });
        };

        Runnable onFailed = () -> {
            removeResidentLoadingIndicator();
            System.err.println("Error loading residents");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    public void populateResidentApplicationRows() {
        addResidentApplicationLoadingIndicator();
        Runnable call = () -> {
            allPendingResidents = residentService.findAllPendingResidents();
            Platform.runLater(() -> {
                removeResidentApplicationLoadingIndicator();
                if (allPendingResidents.isEmpty()) {
                    residentApplicationTableController.clearRow();
                    residentApplicationTableController.showNoData();
                } else {
                    updateResidentApplicationRow(allPendingResidents);
                }
            });
        };

        Runnable onFailed = () -> {
            removeResidentApplicationLoadingIndicator();
            System.err.println("Error loading resident applications");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void performResidentSearch() {
        String searchText = residentListSearchField.getText().trim().toLowerCase();
        searchService.performSearch(
                searchText,
                allResidents,
                searchService.createResidentFilter(searchText),
                (filteredResidents) -> updateResidentRow(filteredResidents));
    }

    private void performResidentApplicationSearch() {
        String searchText = residentApplicationSearchField.getText().trim().toLowerCase();
        searchService.performSearch(
                searchText,
                allPendingResidents,
                searchService.createResidentFilter(searchText),
                (filteredResidents) -> updateResidentApplicationRow(filteredResidents));
    }

    private void updateResidentRow(List<Resident> residents) {
        residentTableController.clearRow();

        if (residents.isEmpty()) {
            residentTableController.showNoData();
            return;
        }
        residents.forEach(resident -> residentTableController.addRow(resident));
    }

    private void updateResidentApplicationRow(List<Resident> residents) {
        residentApplicationTableController.clearRow();

        if (residents.isEmpty()) {
            residentApplicationTableController.showNoData();
            return;
        }
        residents.forEach(resident -> residentApplicationTableController.addRow(resident));
    }

    private void setupListener() {
        addResidentBtn.setOnMouseClicked(_ -> showAddResident());

        residentListSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performResidentSearch());
            searchDelay.playFromStart();
        });

        residentApplicationSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performResidentApplicationSearch());
            searchDelay.playFromStart();
        });
    }

    public void addResidentLoadingIndicator() {
        loadingIndicator = LoadingIndicator.createLoadingIndicator(residentListContent.getWidth(), residentListContent.getHeight());
        if (showApplication) {
            residentListContent.getChildren().add(loadingIndicator);
        } else {
            contentPane.getChildren().add(loadingIndicator);
        }
    }

    public void addResidentApplicationLoadingIndicator() {
        loadingIndicator = LoadingIndicator.createLoadingIndicator(residentApplicationContent.getWidth(), residentApplicationContent.getHeight());
        residentApplicationContent.getChildren().add(loadingIndicator);
    }

    public void removeResidentLoadingIndicator() {
        if (showApplication) {
            residentListContent.getChildren().remove(loadingIndicator);
        } else {
            contentPane.getChildren().remove(loadingIndicator);
        }
    }

    public void removeResidentApplicationLoadingIndicator() {
        residentApplicationContent.getChildren().remove(loadingIndicator);
    }

    private void showAddResident() {
        modalUtils.customizeModal(FXMLPath.ADD_RESIDENT);
    }

    private void triggerPermission() {
        List<RolePermission.Action> allowedActions = RolePermission.getActionByRole(RolePermission.TableActions.RESIDENT, loggedEmployee.getRole());
        if (!allowedActions.contains(RolePermission.Action.CREATE)) addResidentBtn.setDisable(true);
        if (!allowedActions.contains(RolePermission.Action.APPLICATION)) {
            showApplication = false;
            applicationHeader.setManaged(false);
            applicationHeader.setVisible(false);
            residentApplicationContent.setVisible(false);
            residentApplicationContent.setManaged(false);
            residentListContent.setManaged(false);
            residentListContent.setVisible(true);
        }
    }

    private void initializeSSEListener() {
        residentService.listenToUpdates(result -> Platform.runLater(() -> {
            updateResidentRow(result);
            updateApplicationResidentRow(result);
        }));
    }

    private void resetLiveReload() {
        liveReloadUtils.stopListeningToUpdates();
    }

    public void updateResidentRow(String id) {
        Optional<Resident> updatedEmployee = residentService.findResidentById(id);
        updatedEmployee.ifPresentOrElse(resident -> {
            if (!INACTIVE_RESIDENT.contains(resident.getStatus())) {
                residentTableController.updateRow(resident);
            } else {
                residentTableController.deleteRow(resident.getId());

            }
        }, () -> residentTableController.deleteRow(id));
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
}