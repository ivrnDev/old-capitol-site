package com.ivnrdev.connectodo.DTO.Event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventItemsRequestDTO {
    private long id;
    private String eventId;
    private String itemId;
    private String itemName;
    private String quantity;
}
