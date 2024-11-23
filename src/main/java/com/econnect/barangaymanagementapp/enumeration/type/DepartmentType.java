package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.Getter;

import java.util.List;

import static com.econnect.barangaymanagementapp.enumeration.type.RoleType.*;

public enum DepartmentType {
    NONE("None", "", List.of()),
    HUMAN_RESOURCES(
            "Human Resources",
            "view/dashboard.fxml",
            List.of(HR_MANAGER)),
    BARANGAY_OFFICE(
            "Barangay Office",
            "view/dashboard.fxml",
            List.of(SECRETARY, ADMINISTRATIVE_CLERK, OFFICE_FRONT_DESK, FINANCIAL_CLERK, EVENT_COORDINATOR)),
    HEALTH_DEPARTMENT(
            "Health Department",
            "",
            List.of(GENERAL_DOCTOR, DENTAL, MIDWIFE, DNS, BHW, HEALTH_COMMITTEE_HEAD)),
    PUBLIC_UTILITIES_DEPARTMENT(
            "Utility Department",
            "view/dashboard.fxml",
            List.of(UTILITY_HEAD, DELIVERY_ASSISTANT)),
    ;

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

