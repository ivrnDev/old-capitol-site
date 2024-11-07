package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ResidentInfomationType {

    @Getter
    @AllArgsConstructor
    public enum GenderType {
        MALE("Male"),
        FEMALE("Female");

        private String name;

        public static GenderType fromName(String text) {
            for (GenderType gender : GenderType.values()) {
                if (gender.name().equalsIgnoreCase(text)) {
                    return gender;
                }
            }
            throw new IllegalArgumentException("No GenderType found with name: " + text);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum EconomicLevelType {
        POOR("Poor", 0, 12030),
        LOW_INCOME_BUT_NOT_POOR("Low-income but not poor", 12030, 24060),
        LOWER_MIDDLE_INCOME("Lower Middle-Income", 24061, 48120),
        MIDDLE_MIDDLE_INCOME("Middle Middle-Income", 48121, 84210),
        UPPER_MIDDLE_INCOME("Upper Middle-Income", 84211, 144360),
        UPPER_INCOME_BUT_NOT_RICH("Upper-Income but not rich", 144361, 240600),
        RICH("Rich", 240601, Integer.MAX_VALUE);

        private final String name;
        private final int minMonthlyIncome;
        private final int maxMonthlyIncome;

        public static EconomicLevelType fromMonthlyIncome(int income) {
            for (EconomicLevelType level : EconomicLevelType.values()) {
                if (income >= level.minMonthlyIncome && income <= level.maxMonthlyIncome) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Income level not found for monthly income: " + income);
        }

        public String getFormattedRange() {
            String formattedLower = String.format("Php. %,d", minMonthlyIncome);
            String formattedUpper = maxMonthlyIncome == Integer.MAX_VALUE ? "and above" : String.format("Php. %,d", maxMonthlyIncome);
            return formattedLower + " - " + formattedUpper;
        }

        public static EconomicLevelType fromFormattedRange(String formattedRange) {
            if (formattedRange.contains("and above")) {
                String lowerBound = formattedRange.replace("Php. ", "").replace(",", "");
                int minIncome = Integer.parseInt(lowerBound.split(" ")[0]);
                for (EconomicLevelType level : EconomicLevelType.values()) {
                    if (level.minMonthlyIncome == minIncome && level.maxMonthlyIncome == Integer.MAX_VALUE) {
                        return level;
                    }
                }
            } else {
                String[] incomeBounds = formattedRange.replace("Php. ", "").replace(",", "").split(" - ");
                int minIncome = Integer.parseInt(incomeBounds[0]);
                int maxIncome = Integer.parseInt(incomeBounds[1]);

                for (EconomicLevelType level : EconomicLevelType.values()) {
                    if (level.minMonthlyIncome == minIncome && level.maxMonthlyIncome == maxIncome) {
                        return level;
                    }
                }
            }
            throw new IllegalArgumentException("No EconomicLevelType found for range: " + formattedRange);
        }

        public static EconomicLevelType fromName(String name) {
            for (EconomicLevelType level : EconomicLevelType.values()) {
                if (level.getName().equalsIgnoreCase(name)) {
                    return level;
                }
            }
            throw new IllegalArgumentException("No EconomicLevelType found with name: " + name);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum MotherTongue {
        TAGALOG("Tagalog"),
        ENGLISH("English"),
        CEBUANO("Cebuano"),
        ILOCANO("Ilocano"),
        HILIGAYNON("Hiligaynon"),
        WARAY("Waray"),
        KAPAMPANGAN("Kapampangan"),
        BIKOLANO("Bikolano"),
        PANGASINENSE("Pangasinense"),
        MARANAO("Maranao"),
        MAGUINDANAON("Maguindanaon"),
        TAUSUG("Tausug"),
        CHAVACANO("Chavacano");

        private final String name;

        public static MotherTongue fromName(String name) {
            for (MotherTongue tongue : MotherTongue.values()) {
                if (tongue.getName().equalsIgnoreCase(name)) {
                    return tongue;
                }
            }
            throw new IllegalArgumentException("No MotherTongue found with name: " + name);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum BloodType {
        A_POSITIVE("A+"),
        A_NEGATIVE("A-"),
        B_POSITIVE("B+"),
        B_NEGATIVE("B-"),
        AB_POSITIVE("AB+"),
        AB_NEGATIVE("AB-"),
        O_POSITIVE("O+"),
        O_NEGATIVE("O-");

        private final String name;

        public static BloodType fromName(String name) {
            for (BloodType bloodType : BloodType.values()) {
                if (bloodType.getName().equalsIgnoreCase(name)) {
                    return bloodType;
                }
            }
            throw new IllegalArgumentException("No BloodType found with name: " + name);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Religion {
        CATHOLICISM("Catholic"),
        PROTESTANTISM("Protestantism"),
        ISLAM("Islam"),
        EVANGELICAL("Evangelical"),
        INC("Iglesia ni Cristo"),
        BUDDHISM("Buddhism"),
        HINDUISM("Hinduism"),
        BAHA_I("Baha'i Faith"),
        ATHEISM("Atheism"),
        AGNOSTICISM("Agnosticism");

        private final String name;

        public static Religion fromName(String name) {
            for (Religion religion : Religion.values()) {
                if (religion.getName().equalsIgnoreCase(name)) {
                    return religion;
                }
            }
            throw new IllegalArgumentException("No Religion found with name: " + name);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum CivilStatus {
        SINGLE("Single"),
        MARRIED("Married");

        private final String name;

        public static CivilStatus fromName(String name) {
            for (CivilStatus status : CivilStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No CivilStatus found with name: " + name);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum SuffixName {
        NONE("None"),
        JR("Jr."),
        SR("Sr."),
        I("I"),
        II("II"),
        III("III"),
        IV("IV"),
        V("V");

        private final String name;

        public static SuffixName fromName(String name) {
            for (SuffixName suffix : SuffixName.values()) {
                if (suffix.getName().equalsIgnoreCase(name)) {
                    return suffix;
                }
            }
            throw new IllegalArgumentException("No SuffixName found with name: " + name);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ResidencyStatus {
        RENTER("Renter"),
        OWNER("Owner"),
        SHARER("Sharer");

        private final String name;

        public static ResidencyStatus fromName(String name) {
            for (ResidencyStatus status : ResidencyStatus.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No Residency Status found with name: " + name);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ResidentType {
        ADULT("Adult"),
        SENIOR_CITIZEN("Senior Citizen"),
        STUDENT("Student"),
        PWD("Person with Disability");

        private final String name;

        public static ResidentType fromName(String name) {
            for (ResidentType status : ResidentType.values()) {
                if (status.getName().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No Residency Type found with name: " + name);
        }
    }
}
