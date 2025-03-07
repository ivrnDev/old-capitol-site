package com.ivnrdev.connectodo.Service;

import com.ivnrdev.connectodo.Domain.Resident;
import com.ivnrdev.connectodo.Repository.ResidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResidentService {
    private final ResidentRepository residentRepository;

    public Resident saveResident(Resident resident) {
        return residentRepository.save(resident);
    }

    public List<Resident> getAllResidents() {
        return StreamSupport.stream(residentRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Resident updateResidentById(Long id, Resident updatedResident) {
        Optional<Resident> data = residentRepository.findById(id);

        if (data.isEmpty()) {
            throw new RuntimeException("Resident not found");
        }

        Resident existingResident = data.get();

        BeanUtils.copyProperties(updatedResident, existingResident, getNullPropertyNames(updatedResident));
        return residentRepository.save(existingResident);
    }

    private String[] getNullPropertyNames(Resident source) {
        return Arrays.stream(Resident.class.getDeclaredFields())
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
