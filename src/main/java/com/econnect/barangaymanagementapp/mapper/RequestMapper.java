package com.econnect.barangaymanagementapp.mapper;

import com.econnect.barangaymanagementapp.domain.BarangayId;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;

import static com.econnect.barangaymanagementapp.enumeration.type.RequestType.BARANGAY_ID;
import static com.econnect.barangaymanagementapp.enumeration.type.RequestType.CERTIFICATES;

public class RequestMapper {
    public static Request toRequestObject(BarangayId idRequest) {
        return Request.builder()
                .id(idRequest.getId())
                .requestType(BARANGAY_ID)
                .applicationType(idRequest.getApplicationType())
                .createdAt(idRequest.getCreatedAt())
                .updatedAt(idRequest.getUpdatedAt())
                .status(StatusType.RequestStatus.fromName(idRequest.getStatus().getName()))
                .referenceNumber(idRequest.getReferenceNumber())
                .build();
    }

    public static Request toRequestObject(Certificate idRequest) {
        return Request.builder()
                .id(idRequest.getId())
                .requestType(CERTIFICATES)
                .applicationType(idRequest.getApplicationType())
                .createdAt(idRequest.getCreatedAt())
                .updatedAt(idRequest.getUpdatedAt())
                .status(StatusType.RequestStatus.fromName(idRequest.getStatus().getName()))
                .referenceNumber(idRequest.getReferenceNumber())
                .build();
    }
}
