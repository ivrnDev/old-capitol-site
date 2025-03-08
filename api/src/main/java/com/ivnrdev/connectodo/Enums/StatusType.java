package com.ivnrdev.connectodo.Enums;

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
        RELEASING("Releasing"),
        COMPLETED("Completed"),
        REJECTED("Rejected"),
        PROCESSING("Processing"),
        CANCELLED("Cancelled"),
        ONGOING("Ongoing"),
        BORROWED("Borrowed"),
        RETURNED("Returned"),
        OVERDUE("Overdue"),
        WAITING("Waiting"),
        EVALUATION("Evaluation");

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
        REJECTED("Rejected"),
        CANCELLED("Cancelled");


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
        RELEASING("Releasing"),
        COMPLETED("Completed"),
        REJECTED("Rejected"),
        CANCELLED("Cancelled");


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

    @Getter
    public enum CedulaStatus {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        RELEASING("Releasing"),
        COMPLETED("Completed"),
        REJECTED("Rejected"),
        CANCELLED("Cancelled");

        private String name;

        CedulaStatus(String name) {
            this.name = name;
        }

        public static CedulaStatus fromName(String name) {
            for (CedulaStatus status : CedulaStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }
    }

    @Getter
    public enum ComplaintStatus {
        PENDING("Pending"),
        PROCESSING("Processing"),
        UNDER_INVESTIGATION("Under Investigation"),
        RESOLVED("Resolved"),
        REJECTED("Rejected"),
        UNRESOLVED("Unresolved");

        private String name;

        ComplaintStatus(String name) {
            this.name = name;
        }

        public static ComplaintStatus fromName(String name) {
            for (ComplaintStatus status : ComplaintStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }
    }

    @Getter
    public enum InventoryStatus {
        AVAILABLE("Available"),
        UNAVAILABLE("Unavailable"),
        DELETED("Deleted");

        private String name;

        InventoryStatus(String name) {
            this.name = name;
        }

        public static InventoryStatus fromName(String name) {
            for (InventoryStatus status : InventoryStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }
    }

    @Getter
    public enum BorrowStatus {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        RELEASING("Releasing"),
        BORROWED("Borrowed"),
        RETURNED("Returned"),
        OVERDUE("Overdue"),
        REJECTED("Rejected"),
        CANCELLED("Cancelled");

        private String name;

        BorrowStatus(String name) {
            this.name = name;
        }

        public static BorrowStatus fromName(String name) {
            for (BorrowStatus status : BorrowStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }
    }

    @Getter
    public enum EventAppointmentStatus {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        WAITING("Waiting"),
        ONGOING("Ongoing"),
        COMPLETED("Completed"),
        REJECTED("Rejected"),
        CANCELLED("Cancelled");

        private String name;

        EventAppointmentStatus(String name) {
            this.name = name;
        }

        public static EventAppointmentStatus fromName(String name) {
            for (EventAppointmentStatus status : EventAppointmentStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            return null;
        }
    }
}
