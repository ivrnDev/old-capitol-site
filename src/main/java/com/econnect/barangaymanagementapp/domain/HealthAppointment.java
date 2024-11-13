package com.econnect.barangaymanagementapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class HealthAppointment extends BaseEntity {
    private String appointmentDate;
    private String appointmentTime;
    private String healthService;
    private String healthCareProvider;
    private String remarks;
    private String status;
}
