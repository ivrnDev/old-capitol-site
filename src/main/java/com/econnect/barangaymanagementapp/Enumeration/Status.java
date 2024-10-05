package com.econnect.barangaymanagementapp.Enumeration;

public class Status {

    public enum EmployeeStatus {
        PENDING("Pending"), //Encoding  of new employee
        EVALUATION("Evaluation"), //
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        RESIGNED("Resigned"),
        RETIRED("Retired"),
        REJECTED("Rejected"),
        WITHDRAWN("Withdrawn"),
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


