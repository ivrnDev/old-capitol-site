package com.econnect.barangaymanagementapp.domain;

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
public class Event extends BaseEntity {
    private String eventDate;
    private String eventTime;
    private String eventType;
    private String eventPlace;
    private String purpose;
    private StatusType.EventAppointmentStatus status;
    private ApplicationType applicationType;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
