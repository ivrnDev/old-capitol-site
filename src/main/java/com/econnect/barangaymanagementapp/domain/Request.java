package com.econnect.barangaymanagementapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Data
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request extends BaseEntity {
    private String controlNumber;
    private String requestType;
    private String status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
