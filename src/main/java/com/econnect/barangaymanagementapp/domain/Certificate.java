package com.econnect.barangaymanagementapp.domain;

import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Certificate extends BaseEntity {
    private String requestorType;
    private String controlNumber;
    private String request;
    private String purpose;
    private StatusType.CertificateStatus status;
    private String referenceNumber;
    private ApplicationType applicationType;
    private RequestType requestType; // Not included in serialization
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
