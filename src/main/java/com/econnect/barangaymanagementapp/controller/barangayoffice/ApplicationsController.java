package com.econnect.barangaymanagementapp.controller.barangayoffice;


import com.econnect.barangaymanagementapp.controller.shared.BaseApplicationController;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.util.DependencyInjector;

public class ApplicationsController extends BaseApplicationController {

    @Override
    protected FXMLPath applicationTableFXMLPath() {
        return FXMLPath.OFFICE_APPLICATION_TABLE;
    }

    @Override
    protected Object currentControllerInstance() {
        return this;
    }

    public ApplicationsController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

}
