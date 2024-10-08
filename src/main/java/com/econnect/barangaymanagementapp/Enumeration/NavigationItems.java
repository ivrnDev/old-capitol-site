package com.econnect.barangaymanagementapp.Enumeration;

public enum NavigationItems {
    DASHBOARD("Dashboard", "Icon/Sidebar/dashboard.png", "Icon/SidebarSelected/dashboard-selected.png"),
    ANALYTICS("Analytics", "Icon/Sidebar/analytics.png", "Icon/SidebarSelected/analytics-selected.png"),
    EMPLOYEES("Employees", "Icon/Sidebar/employees.png", "Icon/SidebarSelected/employees-selected.png"),
    RESIDENTS("Residents", "Icon/Sidebar/residents.png", "Icon/SidebarSelected/residents-selected.png"),
    HISTORY("History", "Icon/Sidebar/history.png", "Icon/SidebarSelected/history-selected.png"),
    APPLICATIONS("Applications", "Icon/Sidebar/applications.png", "Icon/SidebarSelected/applications-selected.png"),
    ;

    private final String displayName;
    private final String iconPath;
    private final String iconPathSelected;

    NavigationItems(String displayName, String iconPath, String iconPathSelected) {
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
