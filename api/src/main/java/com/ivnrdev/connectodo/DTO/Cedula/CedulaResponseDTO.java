package com.ivnrdev.connectodo.DTO.Cedula;

import com.ivnrdev.connectodo.Enums.ApplicationType;
import com.ivnrdev.connectodo.Enums.RequestType;
import com.ivnrdev.connectodo.Enums.StatusType;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Builder
@Data
public class CedulaResponseDTO {
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
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
