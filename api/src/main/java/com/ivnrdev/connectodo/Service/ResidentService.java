package com.ivnrdev.connectodo.Service;

import com.ivnrdev.connectodo.Domain.Resident;
import com.ivnrdev.connectodo.Repository.ResidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
