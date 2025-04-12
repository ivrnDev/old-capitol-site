package com.ivnrdev.connectodo.Service;

import com.ivnrdev.connectodo.Domain.HealthAppointment;
import com.ivnrdev.connectodo.Repository.HealthAppointmentRepository;
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
public class HealthAppointmentService {
    private final HealthAppointmentRepository healthAppointmentRepository;

    public HealthAppointment saveHealthAppointment(HealthAppointment healthAppointment) {
        return healthAppointmentRepository.save(healthAppointment);
    }

    public List<HealthAppointment> getAllHealthAppointments() {
        return StreamSupport.stream(healthAppointmentRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public HealthAppointment getHealthAppointmentById(Long id) {
        return healthAppointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("HealthAppointment not found"));
    }

    public HealthAppointment deleteHealthAppointmentById(Long id) {
        HealthAppointment existingHealthAppointment = healthAppointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HealthAppointment not found"));

        healthAppointmentRepository.delete(existingHealthAppointment);
        return existingHealthAppointment;
    }

    public HealthAppointment updateHealthAppointment(HealthAppointment healthAppointment) {
        HealthAppointment existingHealthAppointment = healthAppointmentRepository.findById(healthAppointment.getId())
                .orElseThrow(() -> new RuntimeException("HealthAppointment not found"));

        BeanUtils.copyProperties(healthAppointment, existingHealthAppointment, getNullPropertyNames(healthAppointment));
        return healthAppointmentRepository.save(existingHealthAppointment);
    }

    public HealthAppointment updateHealthAppointmentById(Long id, HealthAppointment updatedHealthAppointment) {
        HealthAppointment existingHealthAppointment = healthAppointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HealthAppointment not found"));

        BeanUtils.copyProperties(updatedHealthAppointment, existingHealthAppointment, getNullPropertyNames(updatedHealthAppointment));
        return healthAppointmentRepository.save(updatedHealthAppointment);
    }

    private String[] getNullPropertyNames(HealthAppointment source) {
        return Arrays.stream(HealthAppointment.class.getDeclaredFields())
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
