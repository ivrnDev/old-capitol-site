package com.econnect.barangaymanagementapp.enumeration.type;

public enum NavigationType {
    DASHBOARD("Dashboard", "icon/sidebar/dashboard.png", "icon/sidebar/selected/dashboard-selected.png"),
    ANALYTICS("Analytics", "icon/sidebar/analytics.png", "icon/sidebar/selected/analytics-selected.png"),
    EMPLOYEES("Employees", "icon/sidebar/employees.png", "icon/sidebar/selected/employees-selected.png"),
    RESIDENTS("Residents", "icon/sidebar/residents.png", "icon/sidebar/selected/residents-selected.png"),
    HISTORY("History", "icon/sidebar/history.png", "icon/sidebar/selected/history-selected.png"),
    APPLICATIONS("Applications", "icon/sidebar/applications.png", "icon/sidebar/selected/applications-selected.png"),
    ;

    private final String displayName;
    private final String iconPath;
    private final String iconPathSelected;

    NavigationType(String displayName, String iconPath, String iconPathSelected) {
        this.displayName = displayName;
        this.iconPath = iconPath;
        this.iconPathSelected = iconPathSelected;
    }

    public String getName() {
        return displayName;
    }

    public String getLowerCaseName() {
        return displayName.toLowerCase();
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getIconPathSelected() {
        return iconPathSelected;
    }
}