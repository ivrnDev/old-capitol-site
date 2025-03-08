package com.ivnrdev.connectodo.Domain;

import com.ivnrdev.connectodo.Enums.ApplicationType;
import com.ivnrdev.connectodo.Enums.RequestType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

import static com.ivnrdev.connectodo.Enums.StatusType.CertificateStatus;

@Data
@Builder
@Table("certificates")
public class Certificate {
    private String requestorType;
    private String controlNumber;
    private String request;
    private String purpose;
    private CertificateStatus status;
    private String referenceNumber;
    private ApplicationType applicationType;
    private RequestType requestType;

    @CreatedDate
    private ZonedDateTime createdAt;
    @LastModifiedDate
    private ZonedDateTime updatedAt;
}
