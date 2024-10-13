package com.econnect.barangaymanagementapp.enumeration.type;

public class StatusType {

    public enum EmployeeStatus {
        PENDING("Pending"), //Encoding  of new employee with no requirements
        UNDER_REVIEW("Under Review"),
        EVALUATION("Evaluation"), //
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        RESIGNED("Resigned"),
        RETIRED("Retired"),
        REJECTED("Rejected"),
        WITHDRAWN("Withdrawn"),
        TERMINATED("Terminated"),
        ;

        private String status;

        EmployeeStatus(String status) {
            this.status = status;
        }

        public String getName() {
            return status;
        }

        public static EmployeeStatus fromName(String name) {
            for (EmployeeStatus status : EmployeeStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return PENDING;
        }
    }

}


