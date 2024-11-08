package com.econnect.barangaymanagementapp.enumeration.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResidentRequestFilter {
    CERTIFICATES("Certificates"),
    EVENTS("Events"),
    ;

    private final String name;

    public static ResidentRequestFilter fromName(String name) {
        for (ResidentRequestFilter filter : values()) {
            if (filter.getName().equalsIgnoreCase(name)) {
                return filter;
            }
        }
        return null;
    }

}
