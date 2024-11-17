package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestType {
    ALL("All"),
    CERTIFICATES("Certificates"),
    BARANGAY_ID("Barangay ID"),
    CEDULA("Cedula");
    //    EVENTS("Events"),
//    COMPLAINT("Complaint");

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
