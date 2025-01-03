package com.econnect.barangaymanagementapp.domain;

import com.econnect.barangaymanagementapp.config.deserializer.GenericSerializer;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.BarangayIdStatus;
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
public class BarangayId extends BaseEntity {
    private String height;
    private String weight;
    private String expirationDate;
    private ApplicationType applicationType;
    private BarangayIdStatus status;
    private String referenceNumber;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
