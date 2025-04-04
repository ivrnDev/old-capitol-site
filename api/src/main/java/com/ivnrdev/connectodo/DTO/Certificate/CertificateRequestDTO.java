package com.ivnrdev.connectodo.DTO.Certificate;

import com.ivnrdev.connectodo.Enums.ApplicationType;
import com.ivnrdev.connectodo.Enums.RequestType;
import com.ivnrdev.connectodo.Enums.StatusType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CertificateRequestDTO {
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
}
