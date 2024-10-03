package com.econnect.barangaymanagementapp.Enumeration;

public enum EmployeeType {
    VOLUNTEER("Volunteer"),
    FULL_TIME("Full Time"),
    PART_TIME("Part Time");

    private final String type;

    EmployeeType(String type) {
        this.type = type;
    }
    
    public String getName() {
        return type;
    }
}
