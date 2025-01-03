module com.econnect.barangaymanagementapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires static lombok;
    requires jakarta.mail;
    requires org.bytedeco.javacv;
    requires org.bytedeco.opencv;
    requires org.apache.poi.ooxml;
    requires org.apache.pdfbox;

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

    opens com.econnect.barangaymanagementapp.controller.table.resident to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.table.request to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.table.inventory to javafx.fxml;

    opens com.econnect.barangaymanagementapp.controller.table.employee to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.base to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.detail to javafx.fxml;
    opens com.econnect.barangaymanagementapp.controller.form to javafx.fxml;

}