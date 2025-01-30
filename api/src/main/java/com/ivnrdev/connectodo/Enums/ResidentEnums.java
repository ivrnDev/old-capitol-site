package com.ivnrdev.connectodo.Enums;

import lombok.Getter;

public class ResidentEnums {

    @Getter
    public enum ResidencyStatus {
        RENTER("Renter"),
        OWNER("Owner"),
        SHARER("Sharer");

        private final String name;

        ResidencyStatus(String name) {
            this.name = name;
        }
    }

    @Getter
    public enum ResidentStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive");

        private final String name;

        ResidentStatus(String name) {
            this.name = name;
        }
    }

    @Getter
    public enum BloodType {
        UNKNOWN("Unknown"),
        A_POSITIVE("A+"),
        A_NEGATIVE("A-"),
        B_POSITIVE("B+"),
        B_NEGATIVE("B-"),
        AB_POSITIVE("AB+"),
        AB_NEGATIVE("AB-"),
        O_POSITIVE("O+"),
        O_NEGATIVE("O-");

        private final String name;

        BloodType(String name) {
            this.name = name;
        }
    }

    @Getter
    public enum CivilStatus {
        SINGLE("Singe"),
        MARRIED("Married");

        private final String name;

        CivilStatus(String name) {
            this.name = name;
        }
    }

}
