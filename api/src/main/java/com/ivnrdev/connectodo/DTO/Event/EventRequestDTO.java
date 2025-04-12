package com.ivnrdev.connectodo.DTO.Event;

import com.ivnrdev.connectodo.Enums.ApplicationType;
import com.ivnrdev.connectodo.Enums.StatusType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventRequestDTO {
    private long id;
    private String eventDate;
    private String eventTime;
    private String eventType;
    private String eventPlace;
    private String purpose;
    private StatusType.EventAppointmentStatus status;
    private ApplicationType applicationType;
}
