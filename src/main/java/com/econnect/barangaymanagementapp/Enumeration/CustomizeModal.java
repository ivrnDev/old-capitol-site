package com.econnect.barangaymanagementapp.Enumeration;

public enum CustomizeModal {
    ADD_EMPLOYEE("View/HumanResources/Modal/add-employee.fxml");

    private final String fxmlPath;

    CustomizeModal(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

}
