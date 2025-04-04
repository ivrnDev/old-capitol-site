package com.ivnrdev.connectodo.Mapper;

import com.ivnrdev.connectodo.DTO.Certificate.CertificateRequestDTO;
import com.ivnrdev.connectodo.DTO.Certificate.CertificateResponseDTO;
import com.ivnrdev.connectodo.Domain.Certificate;
import org.springframework.stereotype.Component;

@Component
public class CertificateMapper implements Mapper<Certificate, CertificateRequestDTO, CertificateResponseDTO> {


    @Override
    public Certificate toEntity(CertificateRequestDTO request) {
        return Certificate.builder()
                .id(request.getId())
                .residentId(request.getResidentId())
                .requestorType(request.getRequestorType())
                .controlNumber(request.getControlNumber())
                .request(request.getRequest())
                .purpose(request.getPurpose())
                .status(request.getStatus())
                .referenceNumber(request.getReferenceNumber())
                .applicationType(request.getApplicationType())
                .requestType(request.getRequestType())
                .build();
    }

    @Override
    public CertificateResponseDTO toRes(Certificate certificate) {
        return CertificateResponseDTO.builder()
                .id(certificate.getId())
                .residentId(certificate.getResidentId())
                .requestorType(certificate.getRequestorType())
                .controlNumber(certificate.getControlNumber())
                .request(certificate.getRequest())
                .purpose(certificate.getPurpose())
                .status(certificate.getStatus())
                .referenceNumber(certificate.getReferenceNumber())
                .applicationType(certificate.getApplicationType())
                .requestType(certificate.getRequestType())
                .createdAt(certificate.getCreatedAt())
                .updatedAt(certificate.getUpdatedAt())
                .build();
    }
}
