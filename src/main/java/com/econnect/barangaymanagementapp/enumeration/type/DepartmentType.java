package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.Getter;

import java.util.List;

import static com.econnect.barangaymanagementapp.enumeration.type.RoleType.*;

public enum DepartmentType {
    NONE("None", "", List.of()),
    HUMAN_RESOURCES(
            "Human Resources",
            "view/humanresources/dashboard.fxml",
            List.of(HR_FRONT_DESK)),
    BARANGAY_OFFICE(
            "Barangay Office",
            "view/barangayoffice/dashboard.fxml",
            List.of(SECRETARY, ADMINISTRATIVE_CLERK, OFFICE_FRONT_DESK, FINANCIAL_CLERK, EVENT_COORDINATOR)),
    HEALTH_DEPARTMENT(
            "Health Department",
            "",
            List.of(GENERAL_DOCTOR, DENTAL, MIDWIFE, DNS, BHW, HEALTH_COMMITTEE_HEAD));

    @Getter
    private final String name;
    @Getter
    private final String link;
    @Getter
    private final List<RoleType> roles;

    DepartmentType(String name, String link, List<RoleType> roles) {
        this.name = name;
        this.link = link;
        this.roles = roles;
    }

    public String getDirectoryName() {
        return name.replace(" ", "").toLowerCase();
    }
}

