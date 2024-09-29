package com.econnect.barangaymanagementapp.Enumeration;

public enum Roles {
    //Human Resource Department
    HR_MANAGER("HR Manager"), //Managing the HR Department
    HR_RECRUITER("HR Recruiter"), //Interviewing
    HR_FRONT_DESK("HR Front Desk"), //Receptionist

    //Barangay Office Department
    ADMINISTRATIVE_CLERK("Administrative Clerk"),
    OFFICE_FRONT_DESK("Office Front Desk"),
    RECORDS_CLERK("Records Clerk"),
    FINANCIAL_CLERK("Financial Clerk"),
    EVENT_COORDINATOR("Event Coordinator"),
    CERTIFICATION_CLERK("Certification Clerk"),

    //Finance Department
    TREASURER("Treasurer"),

    //Secretary Department
    SECRETARY("Secretary");

    //Utilities Department


    private final String name;

    Roles(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
