package com.econnect.barangaymanagementapp.domain;

import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.Itemtype;
import com.econnect.barangaymanagementapp.enumeration.type.RequestType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
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
public class Borrow extends BaseEntity {
    private String borrowerId;
    private String itemId;
    private String count;
    private String borrowedDate;
    private String returnedDate;
    private String expectedReturnDate;
    private StatusType.BorrowStatus status;
    private String itemType;
    private ApplicationType applicationType;
    private String referenceNumber;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
