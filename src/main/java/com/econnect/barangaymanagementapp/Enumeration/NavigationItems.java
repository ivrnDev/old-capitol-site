package com.econnect.barangaymanagementapp.Enumeration;

public enum NavigationItems {
    DASHBOARD("Dashboard"),
    ANALYTICS("Analytics"),
    EMPLOYEES("Employees"),
    RESIDENTS("Residents"),
    HISTORY("History"),
    APPLICATIONS("Applications");

    private final String displayName;

    NavigationItems(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return displayName;
    }

    public String getLowerCaseName() {
        return displayName.toLowerCase();
    }
}
