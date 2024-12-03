package com.econnect.barangaymanagementapp.controller.table.request;

import com.econnect.barangaymanagementapp.controller.base.BaseTableController;
import com.econnect.barangaymanagementapp.domain.DepartmentRequest;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEPARTMENT_REQUEST_ROW;

public class DepartmentRequestTableController extends BaseTableController<DepartmentRequest> {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;

    public DepartmentRequestTableController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
    }

    @Override
    public void addRow(DepartmentRequest requestData) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(DEPARTMENT_REQUEST_ROW.getFxmlPath(), dependencyInjector);
            HBox residentRow = loader.load();
            DepartmentRequestRowController residentDepartmentRequestRowController = loader.getController();
            residentRow.setUserData(residentDepartmentRequestRowController);
            residentDepartmentRequestRowController.setData(requestData);
            tableContent.getChildren().add(residentRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding request row: " + e.getMessage(), e);
        }
    }

    public void updateRow(DepartmentRequest updatedDepartmentRequest) {
        boolean rowExists = false;
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox residentRow) {
                DepartmentRequestRowController rowController = (DepartmentRequestRowController) residentRow.getUserData();
                if (rowController != null && rowController.getRequestId().equals(updatedDepartmentRequest.getId())) {
                    rowController.setData(updatedDepartmentRequest);
                    rowExists = true;
                    break;
                }
            }
        }

        if (!rowExists) {
            super.removeNoDataRow();
            addRow(updatedDepartmentRequest);
        }
    }

    public void deleteRow(String requestId) {
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox residentRow) {
                DepartmentRequestRowController rowController = (DepartmentRequestRowController) residentRow.getUserData();

                if (rowController != null && rowController.getRequestId().equals(requestId)) {
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
