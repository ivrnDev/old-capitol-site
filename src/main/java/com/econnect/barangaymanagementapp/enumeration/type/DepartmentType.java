package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.Getter;

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
            List.of(DASHBOARD, ANALYTICS, APPLICATIONS, EMPLOYEES, REQUESTS, RESIDENTS, SERVICES, HISTORY),
            List.of(SECRETARY, ADMINISTRATIVE_CLERK, OFFICE_FRONT_DESK, RECORDS_CLERK, FINANCIAL_CLERK, EVENT_COORDINATOR, CERTIFICATION_CLERK)),
    HEALTH_DEPARTMENT(
            "Health Department",
            "",
            List.of(),
            List.of(GENERAL_DOCTOR, DENTAL, MIDWIFE, DNS, BHW, HEALTH_COMMITTEE_HEAD));

    @Getter
    private final String name;
    @Getter
    private final String link;
    private final List<NavigationType> navigationItemsList;
    @Getter
    private final List<RoleType> roles;

    DepartmentType(String name, String link, List<NavigationType> navigationItemsList, List<RoleType> roles) {
        this.name = name;
        this.link = link;
        this.navigationItemsList = navigationItemsList;
        this.roles = roles;
    }

    public String getDirectoryName() {
        return name.replace(" ", "").toLowerCase();
    }

    public List<NavigationType> getNavigationItems() {
        return navigationItemsList;
    }

}

