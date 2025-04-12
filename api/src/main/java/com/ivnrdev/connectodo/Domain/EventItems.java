package com.ivnrdev.connectodo.Domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Data
@Builder
@Table("event_items")
public class EventItems {
    private long id;
    private String eventId;
    private String itemId;
    private String itemName;
    private String quantity;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
