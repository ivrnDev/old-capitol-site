package com.econnect.barangaymanagementapp.enumeration.ui;

public enum CustomizeModal {
    ADD_EMPLOYEE("view/humanresources/modal/add-employee.fxml"),
    VIEW_EMPLOYEE("view/humanresources/modal/view-employee.fxml");

    private final String fxmlPath;

    CustomizeModal(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

}
