package com.econnect.barangaymanagementapp.enumeration.database;

public enum Firebase {
    EMPLOYEES("/3-Employees"),
    RESIDENTS("Residents"),
    ACCOUNTS("/Accounts"),
    CERTIFICATES("/3-Certificates"),
    BARANGAYID("/3-BarangayId"),
    CEDULA("3-Sedula"),
    COMPLAINT("3-Complaints"),
    HEALTH_PATIENT_ID("6-Health-PatientID"),
    HEALTH_APPOINTMENT("6-Health-Appointments");

    private final String path;

    Firebase(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
