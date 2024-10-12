package com.econnect.barangaymanagementapp.enumeration.type;

import java.util.List;

import static com.econnect.barangaymanagementapp.enumeration.type.NavigationType.*;
import static com.econnect.barangaymanagementapp.enumeration.type.RoleType.*;

public enum DepartmentType {
    NONE("None", "", List.of(), List.of()),
    HUMAN_RESOURCES(
            "Human Resources",
            "view/humanresources/dashboard.fxml",
            List.of(DASHBOARD, ANALYTICS, APPLICATIONS, EMPLOYEES),
            List.of(HR_FRONT_DESK)),
    BARANGAY_OFFICE(
            "Barangay Office",
            "view/barangayoffice/dashboard.fxml",
            List.of(DASHBOARD, ANALYTICS, RESIDENTS, HISTORY),
            List.of(SECRETARY, ADMINISTRATIVE_CLERK, OFFICE_FRONT_DESK, RECORDS_CLERK, FINANCIAL_CLERK, EVENT_COORDINATOR, CERTIFICATION_CLERK));

    private final String name;
    private final String link;
    private final List<NavigationType> navigationItemsList;
    private final List<RoleType> roles;

    DepartmentType(String name, String link, List<NavigationType> navigationItemsList, List<RoleType> roles) {
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

    public List<NavigationType> getNavigationItems() {
        return navigationItemsList;
    }

    public List<RoleType> getRoles() {
        return roles;
    }
}

