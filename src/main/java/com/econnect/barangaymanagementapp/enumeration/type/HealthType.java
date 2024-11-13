package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class HealthType {

    @Getter
    @AllArgsConstructor
    public enum HealthServiceType {
        GENERAL_CHECKUP("General Checkup"),
        FAMILY_PLANNING("Family Planning"),
        PRE_NATAL("Pre-Natal"),
        BABIES_VACCINE("Babies Vaccine"),
        DENTAL("Dental");

        private final String name;

        public static HealthServiceType fromName(String requestType) {
            for (HealthServiceType type : HealthServiceType.values()) {
                if (type.getName().equalsIgnoreCase(requestType)) {
                    return type;
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum HealthcareProvider {
        DOCTOR("Doctor"),
        NURSE("Nurse"),
        MIDWIFE("Midwife");

        private final String name;

        public static HealthServiceType fromName(String requestType) {
            for (HealthServiceType type : HealthServiceType.values()) {
                if (type.getName().equalsIgnoreCase(requestType)) {
                    return type;
                }
            }
            return null;
        }
    }

}
