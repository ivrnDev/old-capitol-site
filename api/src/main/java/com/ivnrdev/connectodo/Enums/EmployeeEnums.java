package com.ivnrdev.connectodo.Enums;

import lombok.Getter;

public class EmployeeEnums {

    @Getter
    public enum RoleType {
        NONE("None"),
        ADMIN("Admin"),

        //Human Resource Department
        HR_MANAGER("HR Manager"),

        ADMINISTRATIVE_CLERK("Administrative Clerk"),
        OFFICE_FRONT_DESK("Office Front Desk"),
        EVENT_COORDINATOR("Event Coordinator"),
        DOCUMENT_CLERK("Document Clerk"),
        SECRETARY("Secretary"),

        WEB_ADMINISTRATOR("Web Administrator"),

        //Health Department
        GENERAL_DOCTOR("General Doctor"),
        DENTAL("Dental"),
        MIDWIFE("Midwife"),
        DNS("Director of Nursing Services"),
        BHW("Barangay Health Worker"),
        HEALTH_COMMITTEE_HEAD("Head of Health Committee"),

        LUPON_MEMBERS("Lupon Members");

        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }

    @Getter
    public enum ApplicationType {
        WALK_IN("Walk-In"),
        ONLINE("Online");

        private final String name;

        ApplicationType(String name) {
            this.name = name;
        }
    }

    @Getter
    public enum EmployeeStatus {
        PENDING("Pending"),
        UNDER_REVIEW("Under Review"),
        EVALUATION("Evaluation"),
        ACTIVE("Active"),
        RESIGNED("Resigned"),
        REJECTED("Rejected"),
        TERMINATED("Terminated");

        private final String name;

        EmployeeStatus(String name) {
            this.name = name;
        }

    }

    @Getter
    public enum EmploymentType {
        FULL_TIME("Full Time"),
        VOLUNTEER("Volunteer"),
        SUSPENDED("Suspended"),
        TERMINATED("Terminated");

        private final String name;

        EmploymentType(String name) {
            this.name = name;
        }
    }
}
