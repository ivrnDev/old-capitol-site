package com.econnect.barangaymanagementapp.controller.humanresources;

import com.econnect.barangaymanagementapp.controller.shared.BaseApplicationController;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.util.DependencyInjector;

public class ApplicationsController extends BaseApplicationController {

    public ApplicationsController(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    @Override
    protected FXMLPath applicationTableFXMLPath() {
        return FXMLPath.HR_EMPLOYEE_APPLICATION_TABLE;
    }

    @Override
    protected Object currentControllerInstance() {
        return this;
    }

}
