package com.econnect.barangaymanagementapp.enumeration.database;

import com.econnect.barangaymanagementapp.domain.Inventory;

public enum Firebase {
    EMPLOYEES("/3-Employees"),
    RESIDENTS("Residents"),
    ACCOUNTS("/Accounts"),
    CERTIFICATES("/3-Certificates"),
    BARANGAYID("/3-BarangayId"),
    CEDULA("3-Sedula"),
    COMPLAINT("3-Complaints"),
    HEALTH_APPOINTMENT("6-Health-Appointments"),
    INVENTORY("3-Inventory");

    private final String path;

    Firebase(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
