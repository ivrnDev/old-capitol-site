package com.econnect.barangaymanagementapp.controller.barangayoffice;

import com.econnect.barangaymanagementapp.controller.barangayoffice.table.application.ApplicationTableController;
import com.econnect.barangaymanagementapp.controller.shared.BaseApplicationController;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.util.DependencyInjector;

public class ApplicationsController extends BaseApplicationController<ApplicationTableController> {

    public ApplicationsController(DependencyInjector dependencyInjector) {
        super(dependencyInjector, FXMLPath.OFFICE_APPLICATION_TABLE);
    }


}
