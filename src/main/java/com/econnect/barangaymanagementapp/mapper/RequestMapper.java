package com.econnect.barangaymanagementapp.mapper;

import com.econnect.barangaymanagementapp.domain.*;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;

import static com.econnect.barangaymanagementapp.enumeration.type.RequestType.*;

public class RequestMapper {
    public static Request toRequestObject(BarangayId request) {
        String residentId = request.getId().substring(0, 12);

        return Request.builder()
                .id(request.getId())
                .residentId(residentId)
                .requestType(BARANGAY_ID)
                .request(BARANGAY_ID.getName())
                .applicationType(request.getApplicationType())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .status(StatusType.RequestStatus.fromName(request.getStatus().getName()))
                .referenceNumber(request.getReferenceNumber())
                .build();
    }

    public static Request toRequestObject(Certificate request) {
        String residentId = request.getId().substring(0, 12);

        return Request.builder()
                .id(request.getId())
                .residentId(residentId)
                .controlNumber(request.getControlNumber())
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
        String residentId = cedula.getId().substring(0, 12);

        return Request.builder()
                .id(cedula.getId())
                .residentId(residentId)
                .requestType(CEDULA)
                .request(CEDULA.getName())
                .applicationType(cedula.getApplicationType())
                .createdAt(cedula.getCreatedAt())
                .updatedAt(cedula.getUpdatedAt())
                .status(StatusType.RequestStatus.fromName(cedula.getStatus().getName()))
                .referenceNumber(cedula.getReferenceNumber())
                .build();
    }

    public static Request toRequestObject(Event event) {
        String residentId = event.getId().substring(0, 12);

        return Request.builder()
                .id(event.getId())
                .residentId(residentId)
                .requestType(EVENTS)
                .request(EVENTS.getName())
                .applicationType(event.getApplicationType())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .status(StatusType.RequestStatus.fromName(event.getStatus().getName()))
                .referenceNumber(event.getId())
                .build();
    }

    public static Request toRequestObject(Borrow borrow) {
        String residentId = borrow.getId().substring(0, 12);
        return Request.builder()
                .id(borrow.getId())
                .residentId(residentId)
                .requestType(BORROWS)
                .request(BORROWS.getName())
                .applicationType(borrow.getApplicationType())
                .createdAt(borrow.getCreatedAt())
                .updatedAt(borrow.getUpdatedAt())
                .status(StatusType.RequestStatus.fromName(borrow.getStatus().getName()))
                .referenceNumber(borrow.getId())
                .build();
    }


}
