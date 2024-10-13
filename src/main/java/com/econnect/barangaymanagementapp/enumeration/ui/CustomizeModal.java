package com.econnect.barangaymanagementapp.enumeration.ui;

public enum CustomizeModal {
    ADD_EMPLOYEE("view/humanresources/modal/add-employee.fxml"),
    HR_VIEW_APPLICATION_EMPLOYEE("view/humanresources/modal/view-application-employee.fxml"),
    OFFICE_VIEW_APPLICATION_EMPLOYEE("view/barangayoffice/modal/view-application-employee.fxml"),
    SETUP_ACCOUNT("view/humanresources/modal/setup-account.fxml"),
    SETUP_REQUIREMENTS("view/barangayoffice/modal/setup-requirements.fxml");

    private final String fxmlPath;

    CustomizeModal(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

}
