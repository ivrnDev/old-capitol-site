module com.econnect.barangaymanagementapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.econnect.barangaymanagementapp.Controller to javafx.fxml;
    exports com.econnect.barangaymanagementapp;
}