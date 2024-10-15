package com.econnect.barangaymanagementapp.enumeration.path;

public enum FXMLPath {
    TABLE_NO_DATA("view/component/no-data-row.fxml"),

    HR_EMPLOYEE_TABLE("view/humanResources/table/employee/employee-table.fxml"),
    HR_EMPLOYEE_ROW("view/humanResources/table/employee/employee-row.fxml"),
    HR_EMPLOYEE_APPLICATION_TABLE("view/humanresources/table/application/application-table.fxml"),
    HR_EMPLOYEE_APPLICATION_ROW("view/humanResources/table/application/application-row.fxml"),

    OFFICE_APPLICATION_TABLE("view/barangayoffice/table/application/application-table.fxml"),
    OFFICE_APPLICATION_ROW("view/barangayoffice/table/application/application-row.fxml"),

    RESOURCE_ERROR_MODAL("view/component/modal/resource-error.fxml"),
    DEFAULT_PROFILE("images/default-profile.png"),

    //Modal
    ADD_EMPLOYEE("view/humanresources/modal/add-employee.fxml"),
    HR_VIEW_APPLICATION_EMPLOYEE("view/humanresources/modal/view-application-employee.fxml"),
    OFFICE_VIEW_APPLICATION_EMPLOYEE("view/barangayoffice/modal/view-application-employee.fxml"),
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
