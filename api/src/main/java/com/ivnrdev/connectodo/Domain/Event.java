package com.ivnrdev.connectodo.Domain;

import com.ivnrdev.connectodo.Enums.ApplicationType;
import com.ivnrdev.connectodo.Enums.StatusType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Data
@Builder
@Table("events")
public class Event {
    private long id;
    private String eventDate;
    private String eventTime;
    private String eventType;
    private String eventPlace;
    private String purpose;
    private StatusType.EventAppointmentStatus status;
    private ApplicationType applicationType;

    @CreatedDate
    private ZonedDateTime createdAt;
    @LastModifiedDate
    private ZonedDateTime updatedAt;
}
