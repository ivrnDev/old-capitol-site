package com.ivnrdev.connectodo.DTO.Employee;

import com.ivnrdev.connectodo.Enums.DepartmentType;
import com.ivnrdev.connectodo.Enums.EmployeeEnums;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class EmployeeResponseDTO {
    private Long id;
    private Long residentId;
    
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String contactNumber;
    private String address;
    private EmployeeEnums.RoleType role;
    private EmployeeEnums.EmployeeStatus status;
    private DepartmentType department;
    private EmployeeEnums.EmploymentType employment;
    private String profileUrl;
    private String resumeUrl;
    private String nbiClearanceUrl;
    private String nbiClearanceExpiration;
    private EmployeeEnums.ApplicationType applicationType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
