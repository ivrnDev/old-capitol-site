package com.econnect.barangaymanagementapp.mapper;

import com.econnect.barangaymanagementapp.domain.BarangayId;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;

import static com.econnect.barangaymanagementapp.enumeration.type.RequestType.BARANGAY_ID;
import static com.econnect.barangaymanagementapp.enumeration.type.RequestType.CERTIFICATES;

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
                .requestType(CERTIFICATES)
                .applicationType(request.getApplicationType())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .status(StatusType.RequestStatus.fromName(request.getStatus().getName()))
                .referenceNumber(request.getReferenceNumber())
                .build();
    }
}
