package com.econnect.barangaymanagementapp.controller.barangayoffice;

import com.econnect.barangaymanagementapp.controller.barangayoffice.table.request.RequestTableController;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private List<Request> allRequest = new ArrayList<>();
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
//        resetLiveReload();
//        initializeSSEListener();

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
            List<Request> fetchedRequests;

            switch (type) {
                case CERTIFICATES -> {
                    fetchedRequests = certificateService.findAllCertificates();
                    fetchedRequests.forEach(request -> request.setRequestType(RequestType.CERTIFICATES));
                }
//                case EVENTS -> {
//                    fetchedRequests = barangayidService.findAllEvents();
//                    fetchedRequests.forEach(request -> request.setRequestType(RequestType.EVENTS));
//                }
//                case BARANGAY_ID -> {
//                    fetchedRequests = barangayidService.findAllBarangayIds();
//                    fetchedRequests.forEach(request -> request.setRequestType(RequestType.BARANGAY_ID));
//                }

                case ALL -> {
                    fetchedRequests = new ArrayList<>();
                    fetchedRequests.addAll(certificateService.findAllCertificates());
//                    fetchedRequests.addAll(barangayidService.findAllEvents());
                }
                default -> fetchedRequests = new ArrayList<>(); // Handle other cases if needed
            }

            allRequest.clear();
            allRequest.addAll(fetchedRequests);

            Platform.runLater(() -> {
                removeTableLoadingIndicator();
                if (allRequest.isEmpty()) {
                    requestTableController.clearRow();
                    requestTableController.showNoData();
                } else {
                    updateRequestRow(allRequest);
                }
            });
        };

        Runnable onFailed = () -> {
            removeTableLoadingIndicator();
            System.err.println("Error loading request");
        };

        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void performSearch() {
        String searchText = residentRequestSearchField.getText().trim().toLowerCase();
        searchService.performSearch(
                searchText,
                allRequest,
                searchService.createRequestFilter(searchText),
                (filteredResidents) -> updateRequestRow(filteredResidents));
    }

    private void updateRequestRow(List<Request> requests) {
        requestTableController.clearRow();

        if (requests.isEmpty()) {
            requestTableController.showNoData();
            return;
        }
        requests.forEach(request -> requestTableController.addRow(request));
    }

//    public void updateRequestRow(String id) {
//        Optional<Resident> updatedEmployee = residentService.findResidentById(id);
//        updatedEmployee.ifPresentOrElse(request -> {
//            if (!INACTIVE_RESIDENT.contains(request.getStatus())) {
//                requestTableController.updateRow(request);
//            } else {
//                requestTableController.deleteRow(request.getId());
//
//            }
//        }, () -> requestTableController.deleteRow(id));
//    }

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
        residentService.listenToUpdates(result -> Platform.runLater(() -> {
//            updateRequestRow(result);
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