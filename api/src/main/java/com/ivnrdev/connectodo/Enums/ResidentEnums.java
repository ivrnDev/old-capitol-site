package com.ivnrdev.connectodo.Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ResidentEnums {

    @AllArgsConstructor
    @Getter
    public enum ResidencyStatus {
        RENTER("Renter"),
        OWNER("Owner"),
        SHARER("Sharer");
        private final String name;
    }

    @AllArgsConstructor
    @Getter
    public enum ResidentStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive");

        private final String name;
    }

    @AllArgsConstructor
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
    }

    @AllArgsConstructor
    @Getter
    public enum CivilStatus {
        SINGLE("Singe"),
        MARRIED("Married");

        private final String name;
    }

}
