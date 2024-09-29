package com.econnect.barangaymanagementapp.Mapper;

import com.econnect.barangaymanagementapp.DTO.EmployeeDTO;
import com.econnect.barangaymanagementapp.Domain.Employee;

public class EmployeeMapper {
    public EmployeeDTO toDto(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setPosition(employee.getPosition());
        employeeDTO.setEmail(employee.getEmail());
        employeeDTO.setContactNumber(employee.getContactNumber());
        employeeDTO.setAddress(employee.getAddress());

        // Convert enum to String
        employeeDTO.setGender(employee.getGender() != null ? employee.getGender() : null);
        employeeDTO.setUsername(employee.getUsername());
        employeeDTO.setRole(employee.getRole());

        // Convert EmployeeStatus enum to String
        employeeDTO.setStatus(employee.getStatus() != null ? employee.getStatus() : null);

        // Convert Departments enum to String
        employeeDTO.setDepartment(employee.getDepartment() != null ? employee.getDepartment() : null);

        employeeDTO.setCreatedAt(employee.getCreatedAt());
        employeeDTO.setUpdatedAt(employee.getUpdatedAt());
        employeeDTO.setLastLogin(employee.getLastLogin());

        return employeeDTO;
    }

    public Employee fromDto(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setId(employeeDTO.getId());
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setPosition(employeeDTO.getPosition());
        employee.setEmail(employeeDTO.getEmail());
        employee.setContactNumber(employeeDTO.getContactNumber());
        employee.setAddress(employeeDTO.getAddress());

        employee.setGender(employeeDTO.getGender() != null ? employeeDTO.getGender() : null);
        employee.setRole(employeeDTO.getRole());
        employee.setUsername(employeeDTO.getUsername());

        employee.setStatus(employeeDTO.getStatus() != null ? employeeDTO.getStatus() : null);

        employee.setDepartment(employeeDTO.getDepartment() != null ? employeeDTO.getDepartment() : null);

        employee.setCreatedAt(employeeDTO.getCreatedAt());
        employee.setUpdatedAt(employeeDTO.getUpdatedAt());
        employee.setLastLogin(employeeDTO.getLastLogin());

        return employee;
    }
}
