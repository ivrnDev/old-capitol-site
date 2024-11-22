package com.econnect.barangaymanagementapp.enumeration.path;

public enum FXMLPath {
    TABLE_NO_DATA("view/component/no-data-row.fxml"),
    SETUP_FILE("view/component/setup-file.fxml"),

    //Employee Table
    EMPLOYEE_TABLE("view/shared/table/employee/employee-table.fxml"),
    EMPLOYEE_ROW("view/shared/table/employee/employee-row.fxml"),
    EMPLOYEE_APPLICATION_TABLE("view/shared/table/application/application-table.fxml"),
    EMPLOYEE_APPLICATION_ROW("view/shared/table/application/application-row.fxml"),

    // Resident Table
    RESIDENT_TABLE("view/barangayoffice/table/resident/resident-table.fxml"),
    RESIDENT_ROW("view/barangayoffice/table/resident/resident-row.fxml"),
    RESIDENT_APPLICATION_TABLE("view/barangayoffice/table/resident/resident-application-table.fxml"),
    RESIDENT_APPLICATION_ROW("view/barangayoffice/table/resident/resident-application-row.fxml"),

    // Request Table
    REQUEST_TABLE("view/barangayoffice/table/request/request-table.fxml"),
    REQUEST_ROW("view/barangayoffice/table/request/request-row.fxml"),


    RESOURCE_ERROR_MODAL("view/component/modal/resource-error.fxml"),
    DEFAULT_PROFILE("images/default-profile.png"),
    DEFAULT_DOCUMENT("icon/document-icon.png"),

    //Modal
    ADD_EMPLOYEE("view/shared/modal/add-employee.fxml"),
    ADD_RESIDENT("view/barangayoffice/modal/add-resident.fxml"),
    SETUP_ACCOUNT("view/shared/setup-account.fxml"),

    //View Modal
    VIEW_APPLICATION_EMPLOYEE("view/shared/modal/view-application-employee.fxml"),
    VIEW_RESIDENT("view/shared/modal/view-resident.fxml"),
    VIEW_DOCUMENT_REQUEST("view/barangayoffice/modal/view/view-document-request.fxml"),
    VIEW_ID_REQUEST("view/barangayoffice/modal/view/view-id-request.fxml"),
    VIEW_BARANGAY_ID("view/barangayoffice/modal/view/view-id.fxml"),
    PRINT_ID("view/barangayoffice/modal/view/print-id.fxml"),
    PRINT_DOCUMENT("view/barangayoffice/modal/view/print-document.fxml"),

    //Form
    CERTIFICATE_FORM("view/barangayoffice/modal/certificate-form.fxml"),
    APPLY_WORK_FORM("view/barangayoffice/modal/apply-work-form.fxml"),
    ID_FORM("view/barangayoffice/modal/id-form.fxml"),
    CEDULA_FORM("view/barangayoffice/modal/cedula-form.fxml"),
    EVENT_REQUEST_FORM("view/barangayoffice/modal/event-certificate-form.fxml"),
    TOOL_AND_MATERIALS_FORM("view/barangayoffice/modal/tools-and-materials-form.fxml"),
    ASSISTANCE_FORM("view/barangayoffice/modal/assistance-form.fxml"),
    COMPLAINT_FORM("view/barangayoffice/modal/complaint-form.fxml"),
    HEALTH_FORM("view/barangayoffice/modal/health-appointment-form.fxml"),


    // Utilities
    UTIL_REQUEST_TABLE("view/utilitydepartment/table/request/request-table.fxml"),
    UTIL_REQUEST_ROW("view/utilitydepartment/table/request/request-row.fxml"),

    INVENTORY_TABLE("view/utilitydepartment/table/inventory/inventory-table.fxml"),
    INVENTORY_ROW("view/utilitydepartment/table/inventory/inventory-row.fxml"),

    ;

    private final String fxmlPath;

    FXMLPath(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath.toLowerCase();
    }
}
