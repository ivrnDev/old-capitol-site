package com.econnect.barangaymanagementapp.controller.barangayoffice.table.request;

import com.econnect.barangaymanagementapp.controller.shared.base.BaseTableController;
import com.econnect.barangaymanagementapp.domain.RequestTable;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.REQUEST_ROW;

public class RequestTableController extends BaseTableController<RequestTable> {
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
    public void addRow(RequestTable requestData) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(REQUEST_ROW.getFxmlPath(), dependencyInjector);
            HBox residentRow = loader.load();
            RequestRowController residentRowController = loader.getController();
            residentRow.setUserData(residentRowController);
            residentRowController.setData(requestData);
            tableContent.getChildren().add(residentRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding request row: " + e.getMessage(), e);
        }
    }

    public void updateRow(RequestTable updatedRequest) {
        boolean rowExists = false;
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox residentRow) {
                RequestRowController rowController = (RequestRowController) residentRow.getUserData();
                if (rowController != null && rowController.getRequestId().equals(updatedRequest.getRequestId())) {
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

    public void deleteRow(String employeeId) {
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox residentRow) {
                RequestRowController rowController = (RequestRowController) residentRow.getUserData();
                if (rowController.getRequestId().equals(employeeId)) {
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
