module com.econnect.barangaymanagementapp {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.econnect.barangaymanagementapp;
    opens com.econnect.barangaymanagementapp.Controller to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.Components to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.HR to javafx.fxml;
}