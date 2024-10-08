package com.econnect.barangaymanagementapp.enumeration.type;

public enum GenderType {
    MALE("Male"),
    FEMALE("Female");

    private String name;

    GenderType(String gender) {
        this.name = gender;
    }

    public String getName() {
        return name;
    }

    public static GenderType fromString(String text) {
        if (text == null) {
            return null;
        }
        switch (text.toUpperCase()) {
            case "MALE":
                return MALE;
            case "FEMALE":
                return FEMALE;
            default:
                throw new IllegalArgumentException("No constant with text " + text + " found");
        }
    }
}
