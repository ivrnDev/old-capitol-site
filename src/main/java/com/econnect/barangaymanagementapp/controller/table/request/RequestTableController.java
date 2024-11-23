package com.econnect.barangaymanagementapp.controller.table.request;

import com.econnect.barangaymanagementapp.controller.base.BaseTableController;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.REQUEST_ROW;

public class RequestTableController extends BaseTableController<Request> {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;

    public RequestTableController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
    }

    @Override
    public void addRow(Request requestData) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(REQUEST_ROW.getFxmlPath(), dependencyInjector);
            HBox residentRow = loader.load();
            RequestRowController requestRowController = loader.getController();
            residentRow.setUserData(requestRowController);
            requestRowController.setData(requestData);
            tableContent.getChildren().add(residentRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding request row: " + e.getMessage(), e);
        }
    }

    public void updateRow(Request updatedRequest) {
        boolean rowExists = false;
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox residentRow) {
                RequestRowController rowController = (RequestRowController) residentRow.getUserData();
                if (rowController != null && rowController.getRequestId().equals(updatedRequest.getId()) && rowController.getRequest().getRequestType().equals(updatedRequest.getRequestType())) {
                    rowController.setData(updatedRequest);
                    rowExists = true;
                    break;
                }
            }
        }

        if (!rowExists) {
            super.removeNoDataRow();
            addRow(updatedRequest);
        }
    }

    public void deleteRow(String requestId, RequestType requestType) {
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox residentRow) {
                RequestRowController rowController = (RequestRowController) residentRow.getUserData();

                if (rowController != null && rowController.getRequestId().equals(requestId) && rowController.getRequest().getRequestType().equals(requestType)) {
                    tableContent.getChildren().remove(residentRow);
                    break;
                }
            }
        }

        if (tableContent.getChildren().isEmpty()) {
            super.showNoData();
        }
    }
}
