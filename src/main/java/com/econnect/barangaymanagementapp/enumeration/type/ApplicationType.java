package com.econnect.barangaymanagementapp.enumeration.type;

public enum ApplicationType {
    WALK_IN("Walk-in"),
    ONLINE("Online");

    private String name;

    ApplicationType(String applicationType) {
        this.name = applicationType;
    }

    public String getName() {
        return name;
    }
}