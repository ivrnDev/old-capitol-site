package com.econnect.barangaymanagementapp.Enumeration;

public enum Modal {
    DEFAULT("View/Component/Modal/default.fxml"),
    CONFIRM("View/Component/Modal/confirm.fxml"),
    APPROVE("View/Component/Modal/approve.fxml"),
    REJECT("View/Component/Modal/reject.fxml"),
    ERROR("View/Component/Modal/error.fxml"),
    SUCCESS("View/Component/Modal/success.fxml");

    private final String link;

    Modal(String link) {
        this.link = link;
    }

    public String getFXMLPath() {
        return link;
    }
}
