package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.NavigationType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static com.econnect.barangaymanagementapp.enumeration.type.NavigationType.*;
import static com.econnect.barangaymanagementapp.enumeration.type.RoleType.*;
import static com.econnect.barangaymanagementapp.util.RolePermission.Action.*;

public class RolePermission {
    private static final Map<DepartmentType, Map<RoleType, List<NavigationType>>> roleNavigationPermissions = Map.of(
            DepartmentType.BARANGAY_OFFICE, Map.of(
                    ADMIN, List.of(DASHBOARD, ANALYTICS, APPLICATIONS, EMPLOYEES, REQUESTS, RESIDENTS, SERVICES, HISTORY),
                    SECRETARY, List.of(DASHBOARD, ANALYTICS, APPLICATIONS, EMPLOYEES, REQUESTS, RESIDENTS, SERVICES, HISTORY),
                    ADMINISTRATIVE_CLERK, List.of(DASHBOARD, ANALYTICS, REQUESTS, RESIDENTS, HISTORY),
                    OFFICE_FRONT_DESK, List.of(DASHBOARD, RESIDENTS, SERVICES),
                    FINANCIAL_CLERK, List.of(DASHBOARD, HISTORY),
                    EVENT_COORDINATOR, List.of(DASHBOARD)
            ),
            DepartmentType.HUMAN_RESOURCES, Map.of(
                    HR_FRONT_DESK, List.of(DASHBOARD, ANALYTICS, APPLICATIONS, EMPLOYEES)
            )
    );

    private static final Map<DepartmentType, Map<RoleType, List<Action>>> roleActionPermission = Map.of(
            DepartmentType.BARANGAY_OFFICE, Map.of(
                    SECRETARY, List.of(CREATE, UPDATE, DELETE),
                    ADMINISTRATIVE_CLERK, List.of(CREATE, UPDATE, DELETE),
                    OFFICE_FRONT_DESK, List.of(CREATE, UPDATE, DELETE),
                    FINANCIAL_CLERK, List.of(CREATE, UPDATE, DELETE),
                    EVENT_COORDINATOR, List.of(CREATE, UPDATE, DELETE)
            )
    );

    public static List<NavigationType> getNavigationByRole(DepartmentType departmentType, RoleType roleType) {
        return roleNavigationPermissions.get(departmentType).get(roleType);
    }

    @Getter
    @AllArgsConstructor
    public enum Action {
        CREATE("Create"),
        UPDATE("Update"),
        DELETE("Delete"),
        ACCEPT("Accept"),
        REJECT("Reject");
        private final String action;
    }
}


