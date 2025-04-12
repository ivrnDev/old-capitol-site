package com.ivnrdev.connectodo.Service;

import com.ivnrdev.connectodo.Domain.Patient;
import com.ivnrdev.connectodo.Repository.PatientRepository;
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
public class PatientService {
    private final PatientRepository patientRepository;

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return StreamSupport.stream(patientRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public Patient deletePatientById(Long id) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patientRepository.delete(existingPatient);
        return existingPatient;
    }

    public Patient updatePatient(Patient patient) {
        Patient existingPatient = patientRepository.findById(patient.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        BeanUtils.copyProperties(patient, existingPatient, getNullPropertyNames(patient));
        return patientRepository.save(existingPatient);
    }

    public Patient updatePatientById(Long id, Patient updatedPatient) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        BeanUtils.copyProperties(updatedPatient, existingPatient, getNullPropertyNames(updatedPatient));
        return patientRepository.save(existingPatient);
    }

    private String[] getNullPropertyNames(Patient source) {
        return Arrays.stream(Patient.class.getDeclaredFields())
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
