package com.econnect.barangaymanagementapp.controller;

import com.econnect.barangaymanagementapp.controller.table.request.RequestTableController;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
import com.econnect.barangaymanagementapp.mapper.RequestMapper;
import com.econnect.barangaymanagementapp.service.*;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
    private VBox residentRequestContainer;
    @FXML
    private TextField residentRequestSearchField;
    @FXML
    private ComboBox<String> residentRequestComboBox;

    private final DependencyInjector dependencyInjector;
    private final ModalUtils modalUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private RequestTableController requestTableController;
    private final LiveReloadUtils liveReloadUtils;
    private final SearchService<Request> searchService;
    private final ResidentService residentService;
    private final CertificateService certificateService;
    private final BarangayidService barangayidService;
    private final CedulaService cedulaService;
    private final ComplaintService complaintService;

    private final Map<RequestType, List<Request>> requestCache = new ConcurrentHashMap<>();
    private StackPane loadingIndicator;

    private final PauseTransition searchDelay = new PauseTransition(Duration.millis(300));

    public RequestController(DependencyInjector dependencyInjector) {
        this.dependencyInjector = dependencyInjector;
        this.residentService = dependencyInjector.getResidentService();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
        this.searchService = dependencyInjector.getRequestSearchService();
        this.certificateService = dependencyInjector.getCertificateService();
        this.barangayidService = dependencyInjector.getBarangayidService();
        this.cedulaService = dependencyInjector.getCedulaService();
        this.complaintService = dependencyInjector.getComplaintService();
    }

    public void initialize() {
        resetLiveReload();
        initializeSSEListener();

        setupListener();
        loadRequestTable();
        fetchData();
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

    private void fetchData() {
        addTableLoadingIndicator();
        RequestType selectedType = RequestType.fromName(residentRequestComboBox.getValue());
        populateRows(selectedType);
    }

    private void populateRows(RequestType type) {
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
//                requestCache.computeIfAbsent(COMPLAINT, k -> new ArrayList<>()).addAll(allComplaint);
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
                        requestTableController.clearRow();
                        requestTableController.showNoData();
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

    private void performSearch() {
        RequestType selectedType = RequestType.fromName(residentRequestComboBox.getValue());
        String searchText = residentRequestSearchField.getText().trim().toLowerCase();

        searchService.performSearch(
                searchText,
                requestCache.get(selectedType),
                searchService.createRequestFilter(searchText),
                (filteredRequest) -> updateRequestRow(filteredRequest));
    }

    private void updateRequestRow(List<Request> requests) {
        requestTableController.clearRow();

        if (requests.isEmpty()) {
            requestTableController.showNoData();
            return;
        }
        requests.forEach(request -> requestTableController.addRow(request));
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
                requestTableController.updateRow(request);
            }
        }, () -> requestTableController.deleteRow(id, requestType));
    }

    private void setupListener() {
        residentRequestSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDelay.setOnFinished(_ -> performSearch());
            searchDelay.playFromStart();
        });
        residentRequestComboBox.getItems().addAll(Arrays.stream(values()).map(RequestType::getName).toList());
        residentRequestComboBox.setValue(RequestType.ALL.getName());
        residentRequestComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateRows(fromName(newValue));
        });
    }

    //Live Reload
    private void initializeSSEListener() {
        barangayidService.listenToUpdates(result -> Platform.runLater(() -> updateRequestRow(BARANGAY_ID, result)));
        certificateService.listenToUpdates(result -> Platform.runLater(() -> updateRequestRow(CERTIFICATES, result)));
        cedulaService.listenToUpdates(result -> Platform.runLater(() -> updateRequestRow(CEDULA, result)));
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