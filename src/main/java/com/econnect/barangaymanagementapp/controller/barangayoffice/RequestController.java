package com.econnect.barangaymanagementapp.controller.barangayoffice;

import com.econnect.barangaymanagementapp.controller.barangayoffice.table.request.RequestTableController;
import com.econnect.barangaymanagementapp.domain.BarangayId;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
import com.econnect.barangaymanagementapp.mapper.RequestMapper;
import com.econnect.barangaymanagementapp.service.BarangayidService;
import com.econnect.barangaymanagementapp.service.CertificateService;
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

    private List<Request> allRequests = new ArrayList<>();
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
        RequestType selectedType = RequestType.fromName(residentRequestComboBox.getValue());
        populateRows(selectedType);
    }

    private void populateRows(RequestType type) {
        addTableLoadingIndicator();

        Runnable call = () -> {
            List<Request> cachedRequests = requestCache.get(type);

            if (cachedRequests != null) {
                allRequests.clear();
                allRequests.addAll(cachedRequests);
            } else {
                allRequests.clear();
                switch (type) {
                    case CERTIFICATES -> {
                        List<Certificate> fetchedCertificates = certificateService.findAllCertificates();
                        List<Request> mappedCertificatesRequest = fetchedCertificates.stream()
                                .map(RequestMapper::toRequestObject)
                                .toList();
                        allRequests.addAll(mappedCertificatesRequest);
                        requestCache.put(RequestType.CERTIFICATES, mappedCertificatesRequest);
                    }

                    case BARANGAY_ID -> {
                        List<BarangayId> fetchedBarangayId = barangayidService.findAllBarangayIds();
                        List<Request> mappedBarangayIdRequest = fetchedBarangayId.stream()
                                .map(RequestMapper::toRequestObject)
                                .toList();
                        allRequests.addAll(mappedBarangayIdRequest);
                        requestCache.put(RequestType.BARANGAY_ID, mappedBarangayIdRequest);
                    }

                    case ALL -> {
                        List<Request> allFetchedRequests = new ArrayList<>();
                        List<Certificate> allCertificates = certificateService.findAllCertificates();
                        List<BarangayId> allBarangayId = barangayidService.findAllBarangayIds();
                        allFetchedRequests.addAll(allCertificates.stream()
                                .map(RequestMapper::toRequestObject)
                                .toList());
                        allFetchedRequests.addAll(allBarangayId.stream()
                                .map(RequestMapper::toRequestObject)
                                .toList());
                        allRequests.addAll(allFetchedRequests);
                        requestCache.put(RequestType.ALL, allFetchedRequests);
                    }
                }
            }

            Platform.runLater(() -> {
                removeTableLoadingIndicator();
                if (allRequests.isEmpty()) {
                    requestTableController.clearRow();
                    requestTableController.showNoData();
                } else {
                    updateRequestRow(allRequests);
                }
            });
        };

        Runnable onFailed = () -> {
            removeTableLoadingIndicator();
            System.err.println("Error loading request");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    public void clearCacheForType(RequestType type) {
        requestCache.remove(type);
    }

    public void clearAllCaches() {
        requestCache.clear();
    }

    private void performSearch() {
        String searchText = residentRequestSearchField.getText().trim().toLowerCase();
        searchService.performSearch(
                searchText,
                allRequests,
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
        }

        updatedRequest.ifPresentOrElse(request -> {
            requestTableController.updateRow(request);
        }, () -> requestTableController.deleteRow(id));
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