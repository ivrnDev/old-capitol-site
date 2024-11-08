package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestType {
    ALL("All"),
    CERTIFICATES("Certificates"),
    EVENTS("Events");

    private final String name;

    public static RequestType fromName(String name) {
        for (RequestType filter : values()) {
            if (filter.getName().equalsIgnoreCase(name)) {
                return filter;
            }
        }
        return null;
    }

}
