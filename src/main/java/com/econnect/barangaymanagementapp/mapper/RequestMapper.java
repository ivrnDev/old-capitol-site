package com.econnect.barangaymanagementapp.mapper;

import com.econnect.barangaymanagementapp.domain.*;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;

import static com.econnect.barangaymanagementapp.enumeration.type.RequestType.*;

public class RequestMapper {
    public static Request toRequestObject(BarangayId request) {
        int length = request.getId().length();
        String residentId = request.getId().substring(0, length - 5);

        return Request.builder()
                .id(request.getId())
                .residentId(residentId)
                .requestType(BARANGAY_ID)
                .applicationType(request.getApplicationType())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .status(StatusType.RequestStatus.fromName(request.getStatus().getName()))
                .referenceNumber(request.getReferenceNumber())
                .build();
    }

    public static Request toRequestObject(Certificate request) {
        int length = request.getId().length();
        String residentId = request.getId().substring(0, length - 5);

        return Request.builder()
                .id(request.getId())
                .residentId(residentId)
                .request(request.getRequest())
                .requestType(CERTIFICATES)
                .applicationType(request.getApplicationType())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .status(StatusType.RequestStatus.fromName(request.getStatus().getName()))
                .referenceNumber(request.getReferenceNumber())
                .build();
    }

    public static Request toRequestObject(Cedula cedula) {
        int length = cedula.getId().length();
        String residentId = cedula.getId().substring(0, length - 5);

        return Request.builder()
                .id(cedula.getId())
                .residentId(residentId)
                .requestType(CEDULA)
                .applicationType(cedula.getApplicationType())
                .createdAt(cedula.getCreatedAt())
                .updatedAt(cedula.getUpdatedAt())
                .status(StatusType.RequestStatus.fromName(cedula.getStatus().getName()))
                .referenceNumber(cedula.getReferenceNumber())
                .build();
    }
}
