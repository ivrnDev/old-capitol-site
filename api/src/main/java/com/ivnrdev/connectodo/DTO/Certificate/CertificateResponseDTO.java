package com.ivnrdev.connectodo.DTO.Certificate;

import com.ivnrdev.connectodo.Enums.ApplicationType;
import com.ivnrdev.connectodo.Enums.RequestType;
import com.ivnrdev.connectodo.Enums.StatusType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;

@Data
@Builder
public class CertificateResponseDTO {
    private Long id;
    private Long residentId;
    private String requestorType;
    private String controlNumber;
    private String request;
    private String purpose;
    private StatusType.CertificateStatus status;
    private String referenceNumber;
    private ApplicationType applicationType;
    private RequestType requestType;

    @CreatedDate
    private ZonedDateTime createdAt;
    @LastModifiedDate
    private ZonedDateTime updatedAt;
}
