package com.econnect.barangaymanagementapp.Enumeration.Paths;

public enum ApiPath {
    EMPLOYEES("/3-Employees"),
    RESIDENTS("/3-Residents"),
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

    ApiPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
