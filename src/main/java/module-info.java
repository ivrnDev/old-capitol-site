module com.econnect.barangaymanagementapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.mail;
    requires static lombok;

    exports com.econnect.barangaymanagementapp;
    exports com.econnect.barangaymanagementapp.Config;
    exports com.econnect.barangaymanagementapp.Utils;
    exports com.econnect.barangaymanagementapp.Enumeration;
    exports com.econnect.barangaymanagementapp.Controller;
    exports com.econnect.barangaymanagementapp.Domain to com.fasterxml.jackson.databind;
    exports com.econnect.barangaymanagementapp.Enumeration.Paths;
    exports com.econnect.barangaymanagementapp.Config.Deserializer to com.fasterxml.jackson.databind;

    opens com.econnect.barangaymanagementapp.Controller to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.Component to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.HumanResources to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.HumanResources.Modal to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.HumanResources.Table to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.BarangayOffice to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.Component.Modal to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Interface to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Domain to com.fasterxml.jackson.databind;
    exports com.econnect.barangaymanagementapp.Enumeration.Modal;

}