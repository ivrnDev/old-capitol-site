package com.econnect.barangaymanagementapp.controller;

import com.econnect.barangaymanagementapp.controller.table.request.DepartmentRequestTableController;
import com.econnect.barangaymanagementapp.controller.table.request.ResidentRequestTableController;
import com.econnect.barangaymanagementapp.domain.DepartmentRequest;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.econnect.barangaymanagementapp.mapper.RequestMapper;
import com.econnect.barangaymanagementapp.service.*;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
import com.econnect.barangaymanagementapp.util.RolePermission;
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

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEPARTMENT_REQUEST_TABLE;
import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.RESIDENT_REQUEST_TABLE;
import static com.econnect.barangaymanagementapp.enumeration.type.RequestType.*;
import static com.econnect.barangaymanagementapp.util.RolePermission.TableActions.REQUEST_FILTER;

public class RequestController {
    @FXML
    private VBox contentPane;
    @FXML
    private VBox residentRequestContent, departmentRequestContent;
    @FXML
    private TextField residentRequestSearchField, departmentRequestSearchField;
    @FXML
    private ComboBox<String> residentRequestComboBox;
    @FXML
    private AnchorPane departmentRequestHeader;
    private List<RolePermission.Action> allowedActions;

    private final DependencyInjector dependencyInjector;
    private final LiveReloadUtils liveReloadUtils;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final CertificateService certificateService;
    private final CedulaService cedulaService;
    private final BarangayidService barangayidService;
    private final DepartmentRequestService departmentRequestService;
    private final EventService eventService;
    private final BorrowService borrowService;

    private ResidentRequestTableController residentRequestTableController;
    private DepartmentRequestTableController departmentRequestTableController;

    private final SearchService<Request> searchService;
    private final Employee loggedEmployee;

    private StackPane residentRequestLoadingIndicator;
    private StackPane departmentRequestLoadingIndicator;
    private final Map<RequestType, List<Request>> residentRequestCache = new ConcurrentHashMap<>();
    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));
    private boolean showDepartmentRequest = true;
    private boolean dataFetched = false;


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
        this.departmentRequestService = dependencyInjector.getDepartmentRequestService();
        this.eventService = dependencyInjector.getEventService();
        this.borrowService = dependencyInjector.getBorrowService();
    }

    public void initialize() {
        resetLiveReload();
        triggerPermission();
        setupListener();
        loadResidentRequestTable();
        loadDepartmentRequestTable();
        fetchData();
        initializeSSEListener();
    }

    private void loadResidentRequestTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(RESIDENT_REQUEST_TABLE.getFxmlPath(), dependencyInjector);
            Parent residentRequestTable = loader.load();
            residentRequestTableController = loader.getController();
            if (showDepartmentRequest) {
                residentRequestContent.getChildren().add(residentRequestTable);
            } else {
                contentPane.getChildren().add(residentRequestTable);
            }
        } catch (IOException e) {
            System.err.println("Error loading employee table: " + e.getMessage());
        }
    }

    private void loadDepartmentRequestTable() {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(DEPARTMENT_REQUEST_TABLE.getFxmlPath(), dependencyInjector);
            Parent departmentRequestTable = loader.load();
            departmentRequestTableController = loader.getController();
            departmentRequestContent.getChildren().add(departmentRequestTable);
        } catch (IOException e) {
            System.err.println("Error loading department request table: " + e.getMessage());
        }
    }

    private void fetchData() {
        addResidentRequestLoadingIndicator();
        addDepartmentRequestLoadingIndicator();
        RequestType selectedType = RequestType.fromName(residentRequestComboBox.getValue());
        populateResidentRequestRows(selectedType);
        populateDepartmentRequestRows();
    }

    private void populateResidentRequestRows(RequestType type) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                if (dataFetched) {
                    Platform.runLater(() -> {
                        List<Request> cachedRequests = residentRequestCache.get(type);
                        if (cachedRequests != null && !cachedRequests.isEmpty()) {
                            updateResidentRequestRow(cachedRequests);
                        } else {
                            residentRequestTableController.clearRow();
                            residentRequestTableController.showNoData();
                        }
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
                List<Request> allEvents = eventService.findAllEvents().stream()
                        .map(RequestMapper::toRequestObject)
                        .toList();
                List<Request> allBorrows = borrowService.findAllBorrows().stream()
                        .map(RequestMapper::toRequestObject)
                        .toList();


                if (allRequestCertificates.isEmpty() && allRequestBarangayId.isEmpty() && allCedula.isEmpty() && allEvents.isEmpty() && allBorrows.isEmpty()) {
                    Platform.runLater(() -> {
                        residentRequestTableController.clearRow();
                        residentRequestTableController.showNoData();
                    });
                    return null;
                }

                List<RequestType> allowedRequestTypes = RolePermission.getRequestTypeByRole(REQUEST_FILTER, loggedEmployee.getRole());

                if (allowedRequestTypes.contains(CERTIFICATES)) allRequest.addAll(allRequestCertificates);
                if (allowedRequestTypes.contains(BARANGAY_ID)) allRequest.addAll(allRequestBarangayId);
                if (allowedRequestTypes.contains(CEDULA)) allRequest.addAll(allCedula);
                if (allowedRequestTypes.contains(EVENTS)) allRequest.addAll(allEvents);
                if (allowedRequestTypes.contains(BORROWS)) allRequest.addAll(allBorrows);

                residentRequestCache.computeIfAbsent(CERTIFICATES, k -> new ArrayList<>()).addAll(allRequestCertificates);
                residentRequestCache.computeIfAbsent(BARANGAY_ID, k -> new ArrayList<>()).addAll(allRequestBarangayId);
                residentRequestCache.computeIfAbsent(CEDULA, k -> new ArrayList<>()).addAll(allCedula);
                residentRequestCache.computeIfAbsent(EVENTS, k -> new ArrayList<>()).addAll(allEvents);
                residentRequestCache.computeIfAbsent(BORROWS, k -> new ArrayList<>()).addAll(allBorrows);
                residentRequestCache.computeIfAbsent(RequestType.ALL, k -> new ArrayList<>()).addAll(allRequest);

                dataFetched = true;
                return null;
            }

            @Override
            protected void succeeded() {
                removeResidentRequestLoadingIndicator();
                residentRequestComboBox.setDisable(false);
                Platform.runLater(() -> {
                    List<Request> requests = residentRequestCache.get(type);
                    if (requests == null || requests.isEmpty()) {
                        residentRequestTableController.clearRow();
                        residentRequestTableController.showNoData();
                    } else {
                        updateResidentRequestRow(requests);
                    }
                });
            }

            @Override
            protected void failed() {
                removeResidentRequestLoadingIndicator();
                System.out.println("Error loading requests: " + getException().getMessage());
            }

        };
        new Thread(task).start();
    }

    public void populateDepartmentRequestRows() {
        Task<List<DepartmentRequest>> fetchDepartmentRequest = new Task<>() {
            @Override
            protected List<DepartmentRequest> call() {
                return departmentRequestService.findAllDepartmentRequests();
            }

            @Override
            protected void succeeded() {
                List<DepartmentRequest> allDepartmentRequest = getValue();
                removeDepartmentRequestLoadingIndicator();
                updateDepartmentRequestRow(allDepartmentRequest);
            }

            @Override
            protected void failed() {
                removeDepartmentRequestLoadingIndicator();
                System.err.println("Error loading department requests: " + getException().getMessage());
            }

        };

        new Thread(fetchDepartmentRequest).start();
    }

    private void performResidentRequestSearch() {
        RequestType selectedType = RequestType.fromName(residentRequestComboBox.getValue());
        String searchText = residentRequestSearchField.getText().trim().toLowerCase();

        searchService.performSearch(
                searchText,
                residentRequestCache.get(selectedType),
                searchService.createRequestFilter(searchText),
                (filteredRequest) -> updateResidentRequestRow(filteredRequest));
    }

    private void triggerPermission() {
        List<RolePermission.Action> allowedActions = RolePermission.getActionByRole(RolePermission.TableActions.REQUEST, loggedEmployee.getRole());
        if (!allowedActions.contains(RolePermission.Action.DEPARTMENT_REQUEST)) {
            showDepartmentRequest = false;
            departmentRequestHeader.setManaged(false);
            departmentRequestHeader.setVisible(false);
            departmentRequestContent.setVisible(false);
            departmentRequestContent.setManaged(false);
            residentRequestContent.setManaged(false);
            residentRequestContent.setVisible(false);
        }
    }

    private void setupListener() {
        residentRequestSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performResidentRequestSearch());
            searchDelay.playFromStart();
        });

        residentRequestComboBox.getItems().addAll(
                RolePermission.getRequestTypeByRole(REQUEST_FILTER, loggedEmployee.getRole()).stream().map(RequestType::getName).toList());
        residentRequestComboBox.setValue(RequestType.ALL.getName());
        residentRequestComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateResidentRequestRows(fromName(newValue));
        });
    }

    private void initializeSSEListener() {
        barangayidService.listenToUpdates(result -> Platform.runLater(() -> updateResidentRequestRow(BARANGAY_ID, result)));
        certificateService.listenToUpdates(result -> Platform.runLater(() -> updateResidentRequestRow(CERTIFICATES, result)));
        cedulaService.listenToUpdates(result -> Platform.runLater(() -> updateResidentRequestRow(CEDULA, result)));
        eventService.listenToUpdates(result -> Platform.runLater(() -> updateResidentRequestRow(EVENTS, result)));
        borrowService.listenToUpdates(result -> Platform.runLater(() -> updateResidentRequestRow(BORROWS, result)));
        departmentRequestService.listenToUpdates(result -> Platform.runLater(this::populateDepartmentRequestRows));
    }

    private void resetLiveReload() {
        liveReloadUtils.stopListeningToUpdates();
    }

    public void addResidentRequestLoadingIndicator() {
        residentRequestLoadingIndicator = LoadingIndicator.createLoadingIndicator(residentRequestContent.getWidth(), residentRequestContent.getHeight());
        if (showDepartmentRequest) {
            residentRequestContent.getChildren().add(residentRequestLoadingIndicator);
        } else {
            contentPane.getChildren().add(residentRequestLoadingIndicator);
        }
    }

    public void addDepartmentRequestLoadingIndicator() {
        departmentRequestLoadingIndicator = LoadingIndicator.createLoadingIndicator(departmentRequestContent.getWidth(), departmentRequestContent.getHeight());
        departmentRequestContent.getChildren().add(departmentRequestLoadingIndicator);
    }

    public void removeResidentRequestLoadingIndicator() {
        if (showDepartmentRequest) {
            residentRequestContent.getChildren().remove(residentRequestLoadingIndicator);
        } else {
            contentPane.getChildren().remove(residentRequestLoadingIndicator);
        }
    }

    public void removeDepartmentRequestLoadingIndicator() {
        departmentRequestContent.getChildren().remove(departmentRequestLoadingIndicator);
    }

    private void updateResidentRequestRow(List<Request> requests) {
        residentRequestTableController.clearRow();

        if (requests.isEmpty()) {
            residentRequestTableController.showNoData();
            return;
        }
        requests.forEach(request -> residentRequestTableController.addRow(request));
    }

    public void updateResidentRequestRow(RequestType requestType, String id) {
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
            case EVENTS:
                updatedRequest = eventService.findEventById(id).map(RequestMapper::toRequestObject);
                break;
            case BORROWS:
                updatedRequest = borrowService.findBorrowById(id).map(RequestMapper::toRequestObject);
        }

        updatedRequest.ifPresentOrElse(request -> {
            residentRequestCache.computeIfPresent(requestType, (key, cachedRequests) -> {
                cachedRequests.removeIf(req -> req.getId().equals(id) && req.getRequestType().equals(requestType));
                cachedRequests.add(request);
                return cachedRequests;
            });

            residentRequestCache.computeIfPresent(RequestType.ALL, (key, cachedRequests) -> {
                cachedRequests.removeIf(req -> req.getId().equals(id) && req.getRequestType().equals(requestType));
                cachedRequests.add(request);
                return cachedRequests;
            });

            if (request.getRequestType() == RequestType.fromName(residentRequestComboBox.getValue()) || residentRequestComboBox.getValue().equals(RequestType.ALL.getName())) {
                residentRequestTableController.updateRow(request);
            }
        }, () -> residentRequestTableController.deleteRow(id, requestType));
    }

    private void updateDepartmentRequestRow(List<DepartmentRequest> requests) {
        departmentRequestTableController.clearRow();

        if (requests.isEmpty()) {
            departmentRequestTableController.showNoData();
            return;
        }
        requests.forEach(request -> departmentRequestTableController.addRow(request));
    }
}