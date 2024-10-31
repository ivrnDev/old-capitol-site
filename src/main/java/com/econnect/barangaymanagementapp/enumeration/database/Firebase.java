package com.econnect.barangaymanagementapp.enumeration.database;

public enum Firebase {
    EMPLOYEES("/3-Employees"),
    RESIDENTS("Residents"),
    ACCOUNTS("/Accounts"),
    CERTIFICATES("/3-Certificates"),
    BARANGAYID("/3-BarangayId"),
    VISITORS("/3-Visitors"),
    ANNOUNCEMENTS("/3-Announcements"),
    EVENTS("/3-Events"),
    BLOTTERS("/3-Blotters"),
    DOCUMENTS("/3-Documents"),
    EMPLOYEE_LOGS("/3-EmployeeLogs"),
    RESIDENT_LOGS("/3-ResidentLogs"),
    VISITOR_LOGS("/3-VisitorLogs"),
    ANNOUNCEMENT_LOGS("/3-AnnouncementLogs"),
    EVENT_LOGS("/3-EventLogs"),
    BLOTTER_LOGS("/3-BlotterLogs"),
    DOCUMENT_LOGS("/3-DocumentLogs");

    private final String path;

    Firebase(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
