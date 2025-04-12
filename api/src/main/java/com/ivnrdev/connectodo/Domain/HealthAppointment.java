package com.ivnrdev.connectodo.Domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("health_appointments")
public class HealthAppointment {
    private long id;
    private String appointmentDate;
    private String appointmentTime;
    private String healthService;
    private String healthCareProvider;
    private String remarks;
    private String status;
}
