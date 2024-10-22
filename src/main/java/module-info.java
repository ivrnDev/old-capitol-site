module com.econnect.barangaymanagementapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires static lombok;
    requires jakarta.mail;

    exports com.econnect.barangaymanagementapp;
    exports com.econnect.barangaymanagementapp.config;
    exports com.econnect.barangaymanagementapp.util;
    exports com.econnect.barangaymanagementapp.controller;
    exports com.econnect.barangaymanagementapp.enumeration.path;
    exports com.econnect.barangaymanagementapp.util.ui;
    exports com.econnect.barangaymanagementapp.util.resource;
    exports com.econnect.barangaymanagementapp.util.state;
    exports com.econnect.barangaymanagementapp.util.data;
    exports com.econnect.barangaymanagementapp.enumeration.ui;
    exports com.econnect.barangaymanagementapp.enumeration.type;
    exports com.econnect.barangaymanagementapp.config.deserializer to com.fasterxml.jackson.databind;
    exports com.econnect.barangaymanagementapp.domain to com.fasterxml.jackson.databind;
    exports com.econnect.barangaymanagementapp.enumeration.modal;
    exports com.econnect.barangaymanagementapp.enumeration.database;

    opens com.econnect.barangaymanagementapp.interfaces to javafx.fxml;
    opens com.econnect.barangaymanagementapp.domain to com.fasterxml.jackson.databind;

    opens com.econnect.barangaymanagementapp.controller to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.component to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.component.modal to javafx.fxml;

    //Barangay Office
    opens com.econnect.barangaymanagementapp.controller.barangayoffice to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.barangayoffice.table.resident to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.barangayoffice.modal to javafx.fxml;
    //Human Resources
    opens com.econnect.barangaymanagementapp.controller.humanresources to javafx.fxml;

    //Shared
    opens com.econnect.barangaymanagementapp.controller.shared.modal to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.shared.table.employee to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.shared.table.application to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.shared to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.shared.base to javafx.fxml;

}