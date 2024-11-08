package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.Getter;

@Getter
public enum CertificateType {
    BARANGAY_CLEARANCE("Barangay Clearance"),
    CERTIFICATE_OF_INDIGENCY("Certificate of Indigency"),
    CERTIFICATE_OF_RESIDENCY("Certificate of Residency"),
    CEDULA("Cedula"),
    BARANGAY_ID("Barangay ID");

    private final String name;

    CertificateType(String requestType) {
        this.name = requestType;
    }

    public static CertificateType fromName(String requestType) {
        for (CertificateType type : CertificateType.values()) {
            if (type.getName().equalsIgnoreCase(requestType)) {
                return type;
            }
        }
        return null;
    }
}
