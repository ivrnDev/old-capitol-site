package com.ivnrdev.connectodo.Mapper;

import com.ivnrdev.connectodo.DTO.Employee.EmployeeRequestDTO;
import com.ivnrdev.connectodo.DTO.Employee.EmployeeResponseDTO;
import com.ivnrdev.connectodo.Domain.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper implements Mapper<Employee, EmployeeRequestDTO, EmployeeResponseDTO> {

    @Override
    public Employee toEntity(EmployeeRequestDTO request) {
        return Employee.builder()
                .residentId(request.getResidentId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .address(request.getAddress())
                .role(request.getRole())
                .status(request.getStatus())
                .department(request.getDepartment())
                .employment(request.getEmployment())
                .username(request.getUsername())
                .password(request.getPassword()) // Should be encrypted in service
                .profileUrl(request.getProfileUrl())
                .resumeUrl(request.getResumeUrl())
                .nbiClearanceUrl(request.getNbiClearanceUrl())
                .nbiClearanceExpiration(request.getNbiClearanceExpiration())
                .applicationType(request.getApplicationType())
                .build();


    }

    @Override
    public EmployeeResponseDTO toRes(Employee employee) {
        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .residentId(employee.getResidentId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .middleName(employee.getMiddleName())
                .email(employee.getEmail())
                .contactNumber(employee.getContactNumber())
                .address(employee.getAddress())
                .role(employee.getRole())
                .status(employee.getStatus())
                .department(employee.getDepartment())
                .employment(employee.getEmployment())
                .profileUrl(employee.getProfileUrl())
                .resumeUrl(employee.getResumeUrl())
                .nbiClearanceUrl(employee.getNbiClearanceUrl())
                .nbiClearanceExpiration(employee.getNbiClearanceExpiration())
                .applicationType(employee.getApplicationType())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

}
