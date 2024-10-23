package com.econnect.barangaymanagementapp.enumeration.path;

public enum FXMLPath {
    TABLE_NO_DATA("view/component/no-data-row.fxml"),

    EMPLOYEE_TABLE("view/shared/table/employee/employee-table.fxml"),
    EMPLOYEE_ROW("view/shared/table/employee/employee-row.fxml"),
    EMPLOYEE_APPLICATION_TABLE("view/shared/table/application/application-table.fxml"),
    EMPLOYEE_APPLICATION_ROW("view/shared/table/application/application-row.fxml"),

    RESIDENT_TABLE("view/barangayoffice/table/resident/resident-table.fxml"),
    RESIDENT_ROW("view/barangayoffice/table/resident/resident-row.fxml"),

    RESOURCE_ERROR_MODAL("view/component/modal/resource-error.fxml"),
    DEFAULT_PROFILE("images/default-profile.png"),

    //Modal
    ADD_EMPLOYEE("view/shared/modal/add-employee.fxml"),
    ADD_RESIDENT("view/barangayoffice/modal/add-resident.fxml"),
    VIEW_APPLICATION_EMPLOYEE("view/shared/view-application-employee.fxml"),
    SETUP_ACCOUNT("view/shared/setup-account.fxml"),
    SETUP_REQUIREMENTS("view/shared/setup-requirements.fxml");

    private final String fxmlPath;

    FXMLPath(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}
