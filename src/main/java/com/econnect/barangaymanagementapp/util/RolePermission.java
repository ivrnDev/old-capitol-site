package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.NavigationType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;

import java.util.List;
import java.util.Map;

import static com.econnect.barangaymanagementapp.enumeration.type.NavigationType.*;
import static com.econnect.barangaymanagementapp.enumeration.type.RoleType.*;
import static com.econnect.barangaymanagementapp.util.RolePermission.Action.*;

public class RolePermission {
    private static final Map<DepartmentType, Map<RoleType, List<NavigationType>>> roleNavigationPermissions = Map.of(
            DepartmentType.BARANGAY_OFFICE, Map.of(
                    ADMIN, List.of(DASHBOARD, ANALYTICS, APPLICATIONS, EMPLOYEES, INVENTORY, REQUESTS, RESIDENTS, SERVICES, HISTORY),
                    SECRETARY, List.of(DASHBOARD, ANALYTICS, APPLICATIONS, EMPLOYEES, INVENTORY, REQUESTS, RESIDENTS, SERVICES, HISTORY),
                    ADMINISTRATIVE_CLERK, List.of(DASHBOARD, ANALYTICS, REQUESTS, RESIDENTS, HISTORY),
                    DOCUMENT_CLERK, List.of(DASHBOARD, ANALYTICS, REQUESTS, RESIDENTS, HISTORY),
                    OFFICE_FRONT_DESK, List.of(DASHBOARD, RESIDENTS, SERVICES),
                    FINANCIAL_CLERK, List.of(DASHBOARD, HISTORY),
                    EVENT_COORDINATOR, List.of(DASHBOARD)
            ),
            DepartmentType.HUMAN_RESOURCES, Map.of(
                    HR_MANAGER, List.of(DASHBOARD, ANALYTICS, APPLICATIONS, EMPLOYEES)
            ),
            DepartmentType.PUBLIC_UTILITIES_DEPARTMENT, Map.of(
                    ADMIN, List.of(DASHBOARD, ANALYTICS, REQUESTS, INVENTORY)
            )
    );

    private static final Map<TableActions, Map<RoleType, List<Action>>> roleActionPermission = Map.of(
            TableActions.REQUEST, Map.of(
                    ADMIN, List.of(APPROVE, REJECT, CANCEL, RELEASE, COMPLETE, RESTORE),
                    SECRETARY, List.of(APPROVE, REJECT, CANCEL, RELEASE, COMPLETE, RESTORE),
                    ADMINISTRATIVE_CLERK, List.of(APPROVE, REJECT, CANCEL, RELEASE, COMPLETE, RESTORE),
                    DOCUMENT_CLERK, List.of(APPROVE, REJECT, COMPLETE)
            ),
            TableActions.RESIDENT, Map.of(
                    ADMIN, List.of(VERIFY, SUSPEND, RESTORE, DELETE, REJECT, CREATE, APPLICATION, EDIT),
                    SECRETARY, List.of(VERIFY, SUSPEND, RESTORE, DELETE, REJECT, CREATE, APPLICATION, EDIT),
                    ADMINISTRATIVE_CLERK, List.of(VERIFY, SUSPEND, RESTORE, DELETE, REJECT, CREATE, APPLICATION),
                    DOCUMENT_CLERK, List.of()
            )
    );


    public static List<NavigationType> getNavigationByRole(DepartmentType departmentType, RoleType roleType) {
        return roleNavigationPermissions.get(departmentType).get(roleType);
    }

    public static List<Action> getActionByRole(TableActions tableActions, RoleType roleType) {
        return roleActionPermission.get(tableActions).get(roleType);
    }

    public enum Action {
        APPROVE,
        CANCEL,
        REJECT,
        RELEASE,
        COMPLETE,
        RESTORE,
        SUSPEND,
        DELETE,
        VERIFY,
        CREATE,
        APPLICATION,
        EDIT
    }

    public enum TableActions {
        EMPLOYEE,
        RESIDENT,
        REQUEST,
        INVENTORY,
        UTILS_REQUEST
    }
}


