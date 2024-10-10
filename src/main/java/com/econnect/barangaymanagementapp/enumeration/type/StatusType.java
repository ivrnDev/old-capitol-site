package com.econnect.barangaymanagementapp.enumeration.type;

public class StatusType {

    public enum EmployeeStatus {
        PENDING("Pending"), //Encoding  of new employee
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

    }

}


