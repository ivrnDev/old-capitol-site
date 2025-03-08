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
import java.util.Optional;
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
        Optional<Employee> data = employeeRepository.findById(id);

        if (data.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }

        return data.get();
    }

    public Employee updateEmployeeById(Long id, Employee updatedemployee) {
        Optional<Employee> data = employeeRepository.findById(id);

        if (data.isEmpty()) {

            throw new RuntimeException("Employee not found");
        }

        Employee existingEmployee = data.get();

        BeanUtils.copyProperties(updatedemployee, existingEmployee, getNullPropertyNames(updatedemployee));
        return employeeRepository.save(existingEmployee);
    }

    public Employee deleteEmployeeById(Long id) {
        Optional<Employee> data = employeeRepository.findById(id);

        if (data.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }

        Employee existingEmployee = data.get();
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
