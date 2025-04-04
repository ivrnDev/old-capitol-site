package com.ivnrdev.connectodo.Service;

import com.ivnrdev.connectodo.Domain.Certificate;
import com.ivnrdev.connectodo.Repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {
    private final CertificateRepository certificateRepository;

    public Certificate saveCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public List<Certificate> getAllCertificates() {
        return StreamSupport.stream(certificateRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Certificate getCertificateById(Long id) {
        return certificateRepository.findById(id).orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    public Certificate deleteCertificateById(Long id) {
        Certificate existingCertificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        certificateRepository.delete(existingCertificate);
        return existingCertificate;
    }

    public Certificate updateCertificate(Certificate certificate) {
        Certificate existingCertificate = certificateRepository.findById(certificate.getId())
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        BeanUtils.copyProperties(certificate, existingCertificate, getNullPropertyNames(certificate));
        return certificateRepository.save(certificate);
    }

    public Certificate updateCertificateById(Long id, Certificate updatedCertificate) {
        Certificate existingCertificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        BeanUtils.copyProperties(updatedCertificate, existingCertificate, getNullPropertyNames(updatedCertificate));
        return certificateRepository.save(existingCertificate);
    }


    private String[] getNullPropertyNames(Certificate source) {
        return Arrays.stream(Certificate.class.getDeclaredFields())
                .filter(field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(source) == null;
                    } catch (IllegalAccessException e) {
                        return true;
                    }
                })
                .map(Field::getName)
                .toArray(String[]::new);
    }

}
