package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class StatusType {

    @AllArgsConstructor
    @Getter
    public enum EmployeeStatus {
        PENDING("Pending"), //Encoding  of new employee with no requirements
        UNDER_REVIEW("Under Review"),
        EVALUATION("Evaluation"), //
        ACTIVE("Active"),
        RESIGNED("Resigned"),
        REJECTED("Rejected"),
        TERMINATED("Terminated");

        private final String name;

        public static EmployeeStatus fromName(String name) {
            for (EmployeeStatus status : EmployeeStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return PENDING;
        }
    }

    @AllArgsConstructor
    @Getter
    public enum ResidentStatus {
        VERIFIED("Verified"),
        DECEASED("Deceased"),
        MIGRATED("Migrated"),
        SUSPENDED("Suspended"),
        REMOVED("Removed"),
        PENDING("Pending"),
        REJECTED("Rejected");
        private final String name;

        public static ResidentStatus fromName(String name) {
            for (ResidentStatus status : ResidentStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }
    }

    @Getter
    public enum RequestStatus {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        REJECTED("Rejected");

        private String name;

        RequestStatus(String name) {
            this.name = name;
        }

        public static RequestStatus fromName(String name) {
            for (RequestStatus status : RequestStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }

    }

    @Getter
    public enum CertificateStatus {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        RELEASING("Releasing"),
        COMPLETED("Completed"),
        REJECTED("Rejected");

        private String name;

        CertificateStatus(String name) {
            this.name = name;
        }

        public static CertificateStatus fromName(String name) {
            for (CertificateStatus status : CertificateStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }
    }

    @Getter
    public enum BarangayIdStatus {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        REJECTED("Rejected");

        private String name;

        BarangayIdStatus(String name) {
            this.name = name;
        }

        public static BarangayIdStatus fromName(String name) {
            for (BarangayIdStatus status : BarangayIdStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }
    }


}


