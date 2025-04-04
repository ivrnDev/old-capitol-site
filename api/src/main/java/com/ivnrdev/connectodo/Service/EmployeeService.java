package com.ivnrdev.connectodo.Service;

import com.ivnrdev.connectodo.Domain.Employee;
import com.ivnrdev.connectodo.Repository.EmployeeRepository;
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
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return StreamSupport.stream(employeeRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee updateEmployeeById(Long id, Employee updatedEmployee) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        BeanUtils.copyProperties(updatedEmployee, existingEmployee, getNullPropertyNames(updatedEmployee));
        return employeeRepository.save(existingEmployee);
    }

    public Employee deleteEmployeeById(Long id) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employeeRepository.delete(existingEmployee);
        return existingEmployee;
    }

    private String[] getNullPropertyNames(Employee source) {
        return Arrays.stream(Employee.class.getDeclaredFields())
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
