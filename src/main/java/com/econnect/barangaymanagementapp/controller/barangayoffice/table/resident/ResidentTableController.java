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

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.RESIDENT_APPLICATION_ROW;

public class ResidentTableController extends BaseTableController<Resident> {
    private final ResidentController residentController;
    @FXML
    private VBox tableContent;

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
            FXMLLoader loader = fxmlLoaderFactory.createFXMLLoader(RESIDENT_APPLICATION_ROW.getFxmlPath(), dependencyInjector, residentController);
            HBox applicationRow = loader.load();
            ResidentRowController residentApplicationRowController = loader.getController();
            Image defaultImage = super.getImageOrDefault(residentData.getId());
            residentApplicationRowController.setImage(defaultImage);
            residentApplicationRowController.setData(residentData);
            super.loadImage(residentData.getId(), residentData.getProfileUrl(), residentApplicationRowController);
            tableContent.getChildren().add(applicationRow);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding employee row: " + e.getMessage(), e);
        }
    }
}
