package com.econnect.barangaymanagementapp.Enumeration;

import java.util.List;

import static com.econnect.barangaymanagementapp.Enumeration.NavigationItems.*;
import static com.econnect.barangaymanagementapp.Enumeration.Roles.*;

public enum Departments {
    HUMAN_RESOURCES(
            "Human Resources",
            "View/HumanResources/dashboard.fxml",
            List.of(DASHBOARD, ANALYTICS, EMPLOYEES),
            List.of(HR_MANAGER, HR_RECRUITER, HR_FRONT_DESK)),
    BARANGAY_OFFICE(
            "Barangay Office",
            "View/BarangayOffice/dashboard.fxml",
            List.of(DASHBOARD, ANALYTICS, RESIDENTS, HISTORY),
            List.of(ADMINISTRATIVE_CLERK, OFFICE_FRONT_DESK, RECORDS_CLERK, FINANCIAL_CLERK, EVENT_COORDINATOR, CERTIFICATION_CLERK));

    private final String name;
    private final String link;
    private final List<NavigationItems> navigationItemsList;
    private final List<Roles> roles;

    Departments(String name, String link, List<NavigationItems> navigationItemsList, List<Roles> roles) {
        this.name = name;
        this.link = link;
        this.navigationItemsList = navigationItemsList;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public String getDirectoryName() {
        return name.replace(" ", "");
    }

    public String getLink() {
        return link;
    }

    public List<NavigationItems> getNavigationItems() {
        return navigationItemsList;
    }

    public List<Roles> getRoles() {
        return roles;
    }
}

