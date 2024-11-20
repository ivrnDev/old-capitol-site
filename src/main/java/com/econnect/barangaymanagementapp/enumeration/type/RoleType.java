package com.econnect.barangaymanagementapp.enumeration.type;

public enum RoleType {
    NONE("None"),
    ADMIN("Admin"),
    //Human Resource Department
//    HR_MANAGER("HR Manager"), //Managing the HR Department
    HR_FRONT_DESK("HR Front Desk"), //Receptionist

    //Barangay Office Department
    ADMINISTRATIVE_CLERK("Administrative Clerk"),
    OFFICE_FRONT_DESK("Office Front Desk"),
    FINANCIAL_CLERK("Financial Clerk"),
    EVENT_COORDINATOR("Event Coordinator"),
    DOCUMENT_CLERK("Document Clerk"),
    SECRETARY("Secretary"),

    //Finance Department
    TREASURER("Treasurer"),

    //Health Department
    GENERAL_DOCTOR("General Doctor"),
    DENTAL("Dental"),
    MIDWIFE("Midwife"),
    DNS("Director of Nursing Services"),
    BHW("Barangay Health Worker"),
    HEALTH_COMMITTEE_HEAD("Head of Health Committee"),
    ;

    private final String name;

    RoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RoleType fromName(String name) {
        for (RoleType role : RoleType.values()) {
            if (role.getName().equalsIgnoreCase(name)) {
                return role;
            }
        }
        return RoleType.NONE;
    }
}
