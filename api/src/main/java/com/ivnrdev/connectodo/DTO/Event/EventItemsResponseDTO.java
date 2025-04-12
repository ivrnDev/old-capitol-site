package com.ivnrdev.connectodo.DTO.Event;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class EventItemsResponseDTO {
    private long id;
    private String eventId;
    private String itemId;
    private String itemName;
    private String quantity;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
