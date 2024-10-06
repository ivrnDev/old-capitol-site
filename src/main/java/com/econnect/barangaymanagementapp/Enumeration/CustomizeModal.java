package com.econnect.barangaymanagementapp.Enumeration;

public enum CustomizeModal {
    ADD_EMPLOYEE("View/HumanResources/Modal/add-employee.fxml"),
    VIEW_EMPLOYEE("View/HumanResources/Modal/view-employee.fxml");

    private final String fxmlPath;

    CustomizeModal(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

}
