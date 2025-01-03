package com.econnect.barangaymanagementapp.enumeration.type;


public enum EmploymentType {
    VOLUNTEER("Volunteer"),
    FULL_TIME("Full Time");

    private final String type;

    EmploymentType(String type) {
        this.type = type;
    }

    public String getName() {
        return type;
    }
}
