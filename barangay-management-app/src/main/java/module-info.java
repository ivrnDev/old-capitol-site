module com.econnect.barangaymanagementapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.econnect.barangaymanagementapp to javafx.fxml;
    exports com.econnect.barangaymanagementapp;
    exports com.econnect.barangaymanagementapp.Controller;
}