package com.econnect.barangaymanagementapp.controller;

import com.econnect.barangaymanagementapp.controller.table.request.ResidentRequestTableController;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
import com.econnect.barangaymanagementapp.mapper.RequestMapper;
import com.econnect.barangaymanagementapp.service.BarangayidService;
import com.econnect.barangaymanagementapp.service.CedulaService;
import com.econnect.barangaymanagementapp.service.CertificateService;
import com.econnect.barangaymanagementapp.service.SearchService;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.REQUEST_TABLE;
import static com.econnect.barangaymanagementapp.enumeration.type.RequestType.*;

public class RequestController {
    @FXML
    private VBox contentPane;
    @FXML
    private VBox residentRequestContent, departmentRequestContent;
    @FXML
    private TextField residentRequestSearchField, departmentRequestSearchField;
    @FXML
    public Button addResidentBtn;
    @FXML
    private ComboBox<String> residentRequestComboBox;
    @FXML
    private AnchorPane applicationHeader;

    private final DependencyInjector dependencyInjector;
    private final LiveReloadUtils liveReloadUtils;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final CertificateService certificateService;
    private final CedulaService cedulaService;
    private final BarangayidService barangayidService;

    private ResidentRequestTableController residentRequestTableController;

    private final SearchService<Request> searchService;
    private final Employee loggedEmployee;

    private List<Request> allRequests;
    private List<Resident> allPendingResidents;
    private boolean showDepartmentRequest;
    private StackPane loadingIndicator;
    private final Map<RequestType, List<Request>> requestCache = new ConcurrentHashMap<>();
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public RequestController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.searchService = dependencyInjector.getRequestSearchService();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
        this.loggedEmployee = UserSession.getInstance().getCurrentEmployee();
        this.certificateService = dependencyInjector.getCertificateService();
        this.cedulaService = dependencyInjector.getCedulaService();
        this.barangayidService = dependencyInjector.getBarangayidService();

    }

    public void initialize() {
        resetLiveReload();
        setupListener();
        loadResidentRequestTable();
        fetchData();
        initializeSSEListener();
    }

    private void loadResidentRequestTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(REQUEST_TABLE.getFxmlPath(), dependencyInjector);
            Parent residentRequestTable = loader.load();
            residentRequestTableController = loader.getController();
            residentRequestContent.getChildren().add(residentRequestTable);
        } catch (IOException e) {
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    private void fetchData() {
        addResidentRequestLoadingIndicator();
        RequestType selectedType = RequestType.fromName(residentRequestComboBox.getValue());
        populateResidentRequestRows(selectedType);
    }

    private void populateResidentRequestRows(RequestType type) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<Request> cachedRequests = requestCache.get(type);

                if (cachedRequests != null && !cachedRequests.isEmpty()) {
                    Platform.runLater(() -> {
                        updateRequestRow(cachedRequests);
                    });
                    return null;
                }

                List<Request> allRequest = new ArrayList<>();
                List<Request> allRequestCertificates = certificateService.findAllCertificates().stream()
                        .map(RequestMapper::toRequestObject)
                        .toList();
                List<Request> allRequestBarangayId = barangayidService.findAllBarangayIds().stream()
                        .map(RequestMapper::toRequestObject)
                        .toList();
                List<Request> allCedula = cedulaService.findAllCedulas().stream()
                        .map(RequestMapper::toRequestObject)
                        .toList();

                allRequest.addAll(allRequestCertificates);
                allRequest.addAll(allRequestBarangayId);
                allRequest.addAll(allCedula);

                requestCache.computeIfAbsent(CERTIFICATES, k -> new ArrayList<>()).addAll(allRequestCertificates);
                requestCache.computeIfAbsent(BARANGAY_ID, k -> new ArrayList<>()).addAll(allRequestBarangayId);
                requestCache.computeIfAbsent(CEDULA, k -> new ArrayList<>()).addAll(allCedula);
                requestCache.computeIfAbsent(RequestType.ALL, k -> new ArrayList<>()).addAll(allRequest);
                return null;
            }

            @Override
            protected void succeeded() {
                removeTableLoadingIndicator();
                residentRequestComboBox.setDisable(false);
                Platform.runLater(() -> {
                    List<Request> requests = requestCache.get(type);
                    if (requests == null || requests.isEmpty()) {
                        residentRequestTableController.clearRow();
                        residentRequestTableController.showNoData();
                    } else {
                        updateRequestRow(requests);
                    }
                });
            }

            @Override
            protected void failed() {
                removeTableLoadingIndicator();
                System.out.println("Error loading requests: " + getException().getMessage());
            }

        };
        new Thread(task).start();
    }

    private void performResidentRequestSearch() {
        RequestType selectedType = RequestType.fromName(residentRequestComboBox.getValue());
        String searchText = residentRequestSearchField.getText().trim().toLowerCase();

        searchService.performSearch(
                searchText,
                requestCache.get(selectedType),
                searchService.createRequestFilter(searchText),
                (filteredRequest) -> updateRequestRow(filteredRequest));
    }

//    private void triggerPermission() {
//        List<RolePermission.Action> allowedActions = RolePermission.getActionByRole(RolePermission.TableActions.RESIDENT, loggedEmployee.getRole());
//        if (!allowedActions.contains(RolePermission.Action.CREATE)) addResidentBtn.setDisable(true);
//        if (!allowedActions.contains(RolePermission.Action.APPLICATION)) {
//            showDepartmentRequest = false;
//            applicationHeader.setManaged(false);
//            applicationHeader.setVisible(false);
//            residentApplicationContent.setVisible(false);
//            residentApplicationContent.setManaged(false);
//            residentListContent.setManaged(false);
//            residentListContent.setVisible(true);
//        }
//    }

    private void setupListener() {
        residentRequestSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performResidentRequestSearch());
            searchDelay.playFromStart();
        });
        residentRequestComboBox.getItems().addAll(Arrays.stream(values()).map(RequestType::getName).toList());
        residentRequestComboBox.setValue(RequestType.ALL.getName());
        residentRequestComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateResidentRequestRows(fromName(newValue));
        });
    }

    private void initializeSSEListener() {
        barangayidService.listenToUpdates(result -> Platform.runLater(() -> updateRequestRow(BARANGAY_ID, result)));
        certificateService.listenToUpdates(result -> Platform.runLater(() -> updateRequestRow(CERTIFICATES, result)));
        cedulaService.listenToUpdates(result -> Platform.runLater(() -> updateRequestRow(CEDULA, result)));
    }

    private void resetLiveReload() {
        liveReloadUtils.stopListeningToUpdates();
    }

    public void addResidentRequestLoadingIndicator() {
        loadingIndicator = LoadingIndicator.createLoadingIndicator(residentRequestContent.getWidth(), residentRequestContent.getHeight());
        residentRequestContent.getChildren().add(loadingIndicator);
    }

    public void removeTableLoadingIndicator() {
        residentRequestContent.getChildren().remove(loadingIndicator);
    }

    private void updateRequestRow(List<Request> requests) {
        residentRequestTableController.clearRow();

        if (requests.isEmpty()) {
            residentRequestTableController.showNoData();
            return;
        }
        requests.forEach(request -> residentRequestTableController.addRow(request));
    }

    public void updateRequestRow(RequestType requestType, String id) {
        Optional<Request> updatedRequest = Optional.empty();

        switch (requestType) {
            case CERTIFICATES:
                updatedRequest = certificateService.findCertificateById(id).map(RequestMapper::toRequestObject);
                break;
            case BARANGAY_ID:
                updatedRequest = barangayidService.findBarangayIdById(id).map(RequestMapper::toRequestObject);
                break;
            case CEDULA:
                updatedRequest = cedulaService.findCedulaById(id).map(RequestMapper::toRequestObject);
                break;
        }

        updatedRequest.ifPresentOrElse(request -> {
            requestCache.computeIfPresent(requestType, (key, cachedRequests) -> {
                cachedRequests.removeIf(req -> req.getId().equals(id) && req.getRequestType().equals(requestType));
                cachedRequests.add(request);
                return cachedRequests;
            });

            requestCache.computeIfPresent(RequestType.ALL, (key, cachedRequests) -> {
                cachedRequests.removeIf(req -> req.getId().equals(id) && req.getRequestType().equals(requestType));
                cachedRequests.add(request);
                return cachedRequests;
            });

            if (request.getRequestType() == RequestType.fromName(residentRequestComboBox.getValue()) || residentRequestComboBox.getValue().equals(RequestType.ALL.getName())) {
                residentRequestTableController.updateRow(request);
            }
        }, () -> residentRequestTableController.deleteRow(id, requestType));
    }
}