package com.econnect.barangaymanagementapp.Enumeration;

public enum Gender {
    MALE("Male"),
    FEMALE("Female");

    private String name;

    Gender(String gender) {
        this.name = gender;
    }

    public String getName() {
        return name;
    }

    public static Gender fromString(String text) {
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
