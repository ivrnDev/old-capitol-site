package com.econnect.barangaymanagementapp.controller.barangayoffice.table.resident;

import com.econnect.barangaymanagementapp.controller.barangayoffice.ResidentController;
import com.econnect.barangaymanagementapp.controller.shared.base.BaseTableController;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.RESIDENT_ROW;

public class ResidentTableController extends BaseTableController<Resident> {
    @FXML
    private VBox tableContent;

    private final ResidentController residentController;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final DependencyInjector dependencyInjector;

    public ResidentTableController(DependencyInjector dependencyInjector, ResidentController residentController) {
        super(dependencyInjector);
        this.fxmlLoaderFactory = dependencyInjector.getFxmlLoaderFactory();
        this.dependencyInjector = dependencyInjector;
        this.residentController = residentController;
    }

    @Override
    public void addRow(Resident residentData) {
        try {
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(RESIDENT_ROW.getFxmlPath(), dependencyInjector, residentController);
            HBox residentRow = loader.load();
            ResidentRowController residentRowController = loader.getController();
            Image defaultImage = super.getImageOrDefault(residentData.getId());
            residentRowController.setImage(defaultImage);
            residentRowController.setData(residentData);
            super.loadImage(residentData.getId(), residentData.getProfileUrl(), residentRowController);
            tableContent.getChildren().add(residentRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding employee row: " + e.getMessage(), e);
        }
    }
}
