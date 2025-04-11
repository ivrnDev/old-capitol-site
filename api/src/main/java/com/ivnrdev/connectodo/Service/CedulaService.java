package com.ivnrdev.connectodo.Service;

import com.ivnrdev.connectodo.Domain.Cedula;
import com.ivnrdev.connectodo.Repository.CedulaRepository;
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
public class CedulaService {
    private final CedulaRepository cedulaRepository;

    public Cedula saveCedula(Cedula cedula) {
        return cedulaRepository.save(cedula);
    }

    public List<Cedula> getAllCedulas() {
        return StreamSupport.stream(cedulaRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Cedula getCedulaById(Long id) {
        return cedulaRepository.findById(id).orElseThrow(() -> new RuntimeException("Cedula not found"));
    }

    public Cedula deleteCedulaById(Long id) {
        Cedula existingCedula = cedulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cedula not found"));

        cedulaRepository.delete(existingCedula);
        return existingCedula;
    }

    public Cedula updateCedula(Cedula cedula) {
        Cedula existingCedula = cedulaRepository.findById(cedula.getId())
                .orElseThrow(() -> new RuntimeException("Cedula not found"));

        BeanUtils.copyProperties(cedula, existingCedula, getNullPropertyNames(cedula));
        return cedulaRepository.save(existingCedula);
    }

    public Cedula updateCedulaById(Long id, Cedula updatedCedula) {
        Cedula existingCedula = cedulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cedula not found"));

        BeanUtils.copyProperties(updatedCedula, existingCedula, getNullPropertyNames(updatedCedula));
        return cedulaRepository.save(existingCedula);
    }

    private String[] getNullPropertyNames(Cedula source) {
        return Arrays.stream(Cedula.class.getDeclaredFields())
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
