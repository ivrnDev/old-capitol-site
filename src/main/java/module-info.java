module com.econnect.barangaymanagementapp {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.econnect.barangaymanagementapp;
    opens com.econnect.barangaymanagementapp.Controller to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.Components to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.HumanResources to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.BarangayOffice to javafx.fxml;
    opens com.econnect.barangaymanagementapp.Controller.Base to javafx.fxml;
}