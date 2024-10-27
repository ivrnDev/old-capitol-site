package com.econnect.barangaymanagementapp.domain;

import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
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
public class Request extends BaseEntity {
    private String requestorId;
    private String requestorType;
    private String controlNumber;
    private RequestType requestType;
    private String purpose;
    private StatusType.RequestStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
