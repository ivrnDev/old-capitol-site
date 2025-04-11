package com.ivnrdev.connectodo.Controller;

import com.ivnrdev.connectodo.DTO.Certificate.CertificateRequestDTO;
import com.ivnrdev.connectodo.DTO.Certificate.CertificateResponseDTO;
import com.ivnrdev.connectodo.Domain.Certificate;
import com.ivnrdev.connectodo.Mapper.CertificateMapper;
import com.ivnrdev.connectodo.Response.BaseResponse;
import com.ivnrdev.connectodo.Service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/certificate")
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateService certificateService;
    private final CertificateMapper certificateMapper;

    @PostMapping
    public ResponseEntity<BaseResponse<CertificateResponseDTO>> saveCertificate(@RequestBody CertificateRequestDTO certificate) {
        Certificate savedCertificate = certificateService.saveCertificate(certificateMapper.toEntity(certificate));
        CertificateResponseDTO certificateResponseDTO = certificateMapper.toRes(savedCertificate);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(certificateResponseDTO, "Certificate saved successfully")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<CertificateResponseDTO>>> getAllCertificates() {
        List<CertificateResponseDTO> certificates = certificateService.getAllCertificates().stream().map(data -> certificateMapper.toRes(data)).collect(Collectors.toList());
        return ResponseEntity.ok(
                BaseResponse.success(certificates, "Certificates fetched successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CertificateResponseDTO>> getCertificateById(@PathVariable Long id) {
        Certificate certificate = certificateService.getCertificateById(id);
        CertificateResponseDTO certificateResponseDTO = certificateMapper.toRes(certificate);
        return ResponseEntity.ok(
                BaseResponse.success(certificateResponseDTO, "Certificate fetched successfully")
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<CertificateResponseDTO>> updateCertificate(@PathVariable Long id, @RequestBody CertificateRequestDTO certificate) {
        Certificate updatedCertificate = certificateService.updateCertificateById(id, certificateMapper.toEntity(certificate));
        CertificateResponseDTO certificateResponseDTO = certificateMapper.toRes(updatedCertificate);
        return ResponseEntity.ok(
                BaseResponse.success(certificateResponseDTO, "Certificate updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<CertificateResponseDTO>> deleteCertificate(@PathVariable Long id) {
        Certificate deletedCertificate = certificateService.deleteCertificateById(id);
        CertificateResponseDTO certificateResponseDTO = certificateMapper.toRes(deletedCertificate);
        return ResponseEntity.ok(
                BaseResponse.success(certificateResponseDTO, "Certificate deleted successfully")
        );
    }
}
