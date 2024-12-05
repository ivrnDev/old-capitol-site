package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.NavigationType;
import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;

import java.util.List;
import java.util.Map;

import static com.econnect.barangaymanagementapp.enumeration.type.NavigationType.*;
import static com.econnect.barangaymanagementapp.enumeration.type.RequestType.*;
import static com.econnect.barangaymanagementapp.enumeration.type.RoleType.*;
import static com.econnect.barangaymanagementapp.util.RolePermission.Action.*;

public class RolePermission {
    private static final Map<DepartmentType, Map<RoleType, List<NavigationType>>> roleNavigationPermissions = Map.of(
            DepartmentType.BARANGAY_OFFICE, Map.of(
                    ADMIN, List.of(DASHBOARD, APPLICATIONS, EMPLOYEES, INVENTORY, REQUESTS, RESIDENTS, SERVICES),
                    SECRETARY, List.of(DASHBOARD, APPLICATIONS, EMPLOYEES, INVENTORY, REQUESTS, RESIDENTS, SERVICES),
                    ADMINISTRATIVE_CLERK, List.of(DASHBOARD, REQUESTS, RESIDENTS),
                    DOCUMENT_CLERK, List.of(DASHBOARD, REQUESTS, RESIDENTS),
                    OFFICE_FRONT_DESK, List.of(DASHBOARD, REQUESTS, RESIDENTS, SERVICES),
                    HR_MANAGER, List.of(DASHBOARD, APPLICATIONS, EMPLOYEES),
                    UTILITY_HEAD, List.of(DASHBOARD, INVENTORY, REQUESTS)
            )
    );

    private static final Map<TableActions, Map<RoleType, List<Action>>> roleActionPermission = Map.of(
            TableActions.REQUEST, Map.of(
                    ADMIN, List.of(APPROVE, REJECT, CANCEL, RELEASE, COMPLETE, RESTORE, DEPARTMENT_REQUEST),
                    SECRETARY, List.of(APPROVE, REJECT, CANCEL, RELEASE, COMPLETE, RESTORE, DEPARTMENT_REQUEST),
                    ADMINISTRATIVE_CLERK, List.of(APPROVE, REJECT, CANCEL, RELEASE, COMPLETE, RESTORE, DEPARTMENT_REQUEST),
                    DOCUMENT_CLERK, List.of(APPROVE, REJECT, COMPLETE),
                    UTILITY_HEAD, List.of(APPROVE, REJECT, RELEASE, COMPLETE, DEPARTMENT_REQUEST),
                    OFFICE_FRONT_DESK, List.of()
            ),
            TableActions.RESIDENT, Map.of(
                    ADMIN, List.of(VERIFY, SUSPEND, RESTORE, DELETE, REJECT, CREATE, APPLICATION, EDIT),
                    SECRETARY, List.of(VERIFY, SUSPEND, RESTORE, DELETE, REJECT, CREATE, APPLICATION, EDIT),
                    ADMINISTRATIVE_CLERK, List.of(VERIFY, SUSPEND, RESTORE, DELETE, REJECT, CREATE, APPLICATION),
                    DOCUMENT_CLERK, List.of(),
                    OFFICE_FRONT_DESK, List.of()
            )
    );

    private static final Map<TableActions, Map<RoleType, List<RequestType>>> requestTypePermission = Map.of(
            TableActions.REQUEST_FILTER, Map.of(
                    ADMIN, List.of(ALL, CERTIFICATES, BARANGAY_ID, CEDULA, BORROWS, EVENTS),
                    SECRETARY, List.of(ALL, CERTIFICATES, BARANGAY_ID, CEDULA, BORROWS, EVENTS),
                    ADMINISTRATIVE_CLERK, List.of(ALL, CERTIFICATES, BARANGAY_ID, CEDULA, BORROWS, EVENTS),
                    DOCUMENT_CLERK, List.of(ALL, CERTIFICATES, BARANGAY_ID, CEDULA),
                    UTILITY_HEAD, List.of(ALL, BORROWS, EVENTS),
                    OFFICE_FRONT_DESK, List.of(ALL, CERTIFICATES, BARANGAY_ID, CEDULA, BORROWS, EVENTS)
            )
    );


    public static List<NavigationType> getNavigationByRole(DepartmentType departmentType, RoleType roleType) {
        return roleNavigationPermissions.get(departmentType).get(roleType);
    }

    public static List<Action> getActionByRole(TableActions tableActions, RoleType roleType) {
        return roleActionPermission.get(tableActions).get(roleType);
    }

    public static List<RequestType> getRequestTypeByRole(TableActions tableActions, RoleType roleType) {
        return requestTypePermission.get(tableActions).get(roleType);
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
        EDIT,
        DEPARTMENT_REQUEST
    }

    public enum TableActions {
        EMPLOYEE,
        RESIDENT,
        REQUEST,
        INVENTORY,
        UTILS_REQUEST,
        REQUEST_FILTER
    }
}


