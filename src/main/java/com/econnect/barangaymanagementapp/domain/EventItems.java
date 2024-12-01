package com.econnect.barangaymanagementapp.domain;

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
public class EventItems extends BaseEntity {
    private String eventId;
    private String itemId;
    private String itemName;
    private String quantity;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
