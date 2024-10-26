package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.Getter;

@Getter
public enum RequestType {
    BARANGAY_CLEARANCE("Barangay Clearance"),
    CERTIFICATE_OF_INDIGENCY("Certificate of Indigency"),
    CERTIFICATE_OF_RESIDENCY("Certificate of Residency"),
    CEDULA("Cedula"),
    BARANGAY_ID("Barangay ID");

    private final String name;

    RequestType(String requestType) {
        this.name = requestType;
    }

    public static RequestType fromName(String requestType) {
        for (RequestType type : RequestType.values()) {
            if (type.getName().equalsIgnoreCase(requestType)) {
                return type;
            }
        }
        return null;
    }
}
