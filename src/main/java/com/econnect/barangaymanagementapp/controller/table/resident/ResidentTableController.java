package com.econnect.barangaymanagementapp.controller.table.resident;

import com.econnect.barangaymanagementapp.controller.base.BaseTableController;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.RESIDENT_ROW;

public class ResidentTableController extends BaseTableController<Resident> {
    @FXML
    private VBox tableContent;

    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;

    public ResidentTableController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
    }

    @Override
    public void addRow(Resident residentData) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(RESIDENT_ROW.getFxmlPath(), dependencyInjector);
            HBox residentRow = loader.load();
            ResidentRowController residentRowController = loader.getController();
            residentRow.setUserData(residentRowController);
            Image defaultImage = super.getImageOrDefault(residentData.getId());
            residentRowController.setImage(defaultImage);
            residentRowController.setData(residentData);
            super.loadImage(residentData.getId(), residentData.getProfileUrl(), residentRowController, false);
            tableContent.getChildren().add(residentRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding employee row: " + e.getMessage(), e);
        }
    }

    public void updateRow(Resident updatedResident) {
        boolean rowExists = false;
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox residentRow) {
                ResidentRowController rowController = (ResidentRowController) residentRow.getUserData();
                if (rowController != null && rowController.getResidentId().equals(updatedResident.getId())) {
                    rowController.setData(updatedResident);
                    super.loadImage(updatedResident.getId(), updatedResident.getProfileUrl(), rowController, true);
                    rowExists = true;
                    break;
                }
            }
        }

        if (!rowExists) {
            super.removeNoDataRow();
            addRow(updatedResident);
        }
    }

    public void deleteRow(String employeeId) {
        for (Node node : tableContent.getChildren()) {
            if (node instanceof HBox residentRow) {
                ResidentRowController rowController = (ResidentRowController) residentRow.getUserData();
                if (rowController != null && rowController.getResidentId().equals(employeeId)) {
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
