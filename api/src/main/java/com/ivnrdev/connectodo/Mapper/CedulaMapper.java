package com.ivnrdev.connectodo.Mapper;

import com.ivnrdev.connectodo.DTO.Cedula.CedulaRequestDTO;
import com.ivnrdev.connectodo.DTO.Cedula.CedulaResponseDTO;
import com.ivnrdev.connectodo.Domain.Cedula;
import org.springframework.stereotype.Component;

@Component
public class CedulaMapper implements Mapper<Cedula, CedulaRequestDTO, CedulaResponseDTO> {

    @Override
    public Cedula toEntity(CedulaRequestDTO request) {
        return Cedula.builder()
                .id(request.getId())
                .residentId(request.getResidentId())
                .expirationDate(request.getExpirationDate())
                .grossReceipt(request.getGrossReceipt())
                .height(request.getHeight())
                .weight(request.getWeight())
                .totalEarnings(request.getTotalEarnings())
                .status(request.getStatus())
                .referenceNumber(request.getReferenceNumber())
                .applicationType(request.getApplicationType())
                .purpose(request.getPurpose())
                .requestType(request.getRequestType())
                .build();
    }

    @Override
    public CedulaResponseDTO toRes(Cedula certificate) {
        return CedulaResponseDTO.builder()
                .id(certificate.getId())
                .residentId(certificate.getResidentId())
                .expirationDate(certificate.getExpirationDate())
                .grossReceipt(certificate.getGrossReceipt())
                .height(certificate.getHeight())
                .weight(certificate.getWeight())
                .totalEarnings(certificate.getTotalEarnings())
                .status(certificate.getStatus())
                .referenceNumber(certificate.getReferenceNumber())
                .applicationType(certificate.getApplicationType())
                .purpose(certificate.getPurpose())
                .requestType(certificate.getRequestType())
                .createdAt(certificate.getCreatedAt())
                .updatedAt(certificate.getUpdatedAt())
                .build();
    }
}
