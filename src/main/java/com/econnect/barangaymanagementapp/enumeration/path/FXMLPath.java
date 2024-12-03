package com.econnect.barangaymanagementapp.enumeration.path;

public enum FXMLPath {
    TABLE_NO_DATA("view/component/no-data-row.fxml"),
    SETUP_FILE("view/component/setup-file.fxml"),

    EMPLOYEE_TABLE("view/table/employee/employee-table.fxml"),
    EMPLOYEE_ROW("view/table/employee/employee-row.fxml"),
    EMPLOYEE_APPLICATION_TABLE("view/table/employee/employee-application-table.fxml"),
    EMPLOYEE_APPLICATION_ROW("view/table/employee/employee-application-row.fxml"),

    RESIDENT_TABLE("view/table/resident/resident-table.fxml"),
    RESIDENT_ROW("view/table/resident/resident-row.fxml"),
    RESIDENT_APPLICATION_TABLE("view/table/resident/resident-application-table.fxml"),
    RESIDENT_APPLICATION_ROW("view/table/resident/resident-application-row.fxml"),

    RESIDENT_REQUEST_TABLE("view/table/request/resident-request-table.fxml"),
    RESIDENT_REQUEST_ROW("view/table/request/resident-request-row.fxml"),
    DEPARTMENT_REQUEST_TABLE("view/table/request/department-request-table.fxml"),
    DEPARTMENT_REQUEST_ROW("view/table/request/department-request-row.fxml"),

    INVENTORY_TABLE("view/table/inventory/inventory-table.fxml"),
    INVENTORY_ROW("view/table/inventory/inventory-row.fxml"),

    DEFAULT_PROFILE("images/default-profile.png"),
    DEFAULT_DOCUMENT("icon/document-icon.png"),

    VIEW_APPLICATION_EMPLOYEE("view/detail/view-application-employee.fxml"),
    VIEW_RESIDENT("view/detail/view-resident.fxml"),
    VIEW_DOCUMENT_REQUEST("view/detail/view-document-request.fxml"),
    VIEW_ID_REQUEST("view/detail/view-id-request.fxml"),
    VIEW_BARANGAY_ID("view/detail/view-id.fxml"),
    PRINT_ID("view/detail/print-id.fxml"),
    PRINT_DOCUMENT("view/detail/print-document.fxml"),

    ADD_EMPLOYEE("view/form/add-employee.fxml"),
    ADD_RESIDENT("view/form/add-resident.fxml"),
    EDIT_RESIDENT("view/form/edit-resident.fxml"),
    ADD_ITEM("view/form/add-item.fxml"),
    CERTIFICATE_FORM("view/form/certificate-form.fxml"),
    APPLY_WORK_FORM("view/form/apply-work-form.fxml"),
    ID_FORM("view/form/id-form.fxml"),
    CEDULA_FORM("view/form/cedula-form.fxml"),
    EVENT_REQUEST_FORM("view/form/event-form.fxml"),
    TOOL_AND_MATERIALS_FORM("view/form/borrow-form.fxml"),
    ASSISTANCE_FORM("view/form/assistance-form.fxml"),
    COMPLAINT_FORM("view/form/complaint-form.fxml"),
    HEALTH_FORM("view/form/health-appointment-form.fxml"),
    SETUP_ACCOUNT("view/form/setup-account.fxml"),
    EDIT_ACCOUNT("view/form/edit-account.fxml"),
    ;

    private final String fxmlPath;

    FXMLPath(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath.toLowerCase();
    }
}
