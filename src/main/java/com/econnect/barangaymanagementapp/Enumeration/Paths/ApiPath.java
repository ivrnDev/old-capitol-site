package com.econnect.barangaymanagementapp.Enumeration.Paths;

public enum ApiPath {
    EMPLOYEES("/3Employees"),
    RESIDENTS("/3Residents"),
    VISITORS("/3Visitors"),
    ANNOUNCEMENTS("/3Announcements"),
    EVENTS("/3Events"),
    BLOTTERS("/3Blotters"),
    DOCUMENTS("/3Documents"),
    EMPLOYEE_LOGS("/3EmployeeLogs"),
    RESIDENT_LOGS("/3ResidentLogs"),
    VISITOR_LOGS("/3VisitorLogs"),
    ANNOUNCEMENT_LOGS("/3AnnouncementLogs"),
    EVENT_LOGS("/3EventLogs"),
    BLOTTER_LOGS("/3BlotterLogs"),
    DOCUMENT_LOGS("/3DocumentLogs");

    private final String path;

    ApiPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
