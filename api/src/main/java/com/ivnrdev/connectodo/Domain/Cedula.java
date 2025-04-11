package com.ivnrdev.connectodo.Domain;

import com.ivnrdev.connectodo.Enums.ApplicationType;
import com.ivnrdev.connectodo.Enums.RequestType;
import com.ivnrdev.connectodo.Enums.StatusType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Data
@Builder
@Table("cedula")
public class Cedula {
    private long id;
    private long residentId;
    private String height;
    private String weight;
    private String grossReceipt;
    private String totalEarnings;
    private StatusType.CedulaStatus status;
    private String referenceNumber;
    private String expirationDate;
    private ApplicationType applicationType;
    private String purpose;
    private RequestType requestType;

    @CreatedDate
    private ZonedDateTime createdAt;
    @LastModifiedDate
    private ZonedDateTime updatedAt;
}