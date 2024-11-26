package com.econnect.barangaymanagementapp.domain;


import com.econnect.barangaymanagementapp.config.deserializer.GenericSerializer;
import com.econnect.barangaymanagementapp.enumeration.type.ItemAvailability;
import com.econnect.barangaymanagementapp.enumeration.type.Itemtype;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = GenericSerializer.class)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Inventory extends BaseEntity {
    private String itemName;
    private String itemType;
    private String stocks;
    private String itemImageUrl;
    private String status;
    private String availability;
    private String minStock;
    private String maxStock;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
