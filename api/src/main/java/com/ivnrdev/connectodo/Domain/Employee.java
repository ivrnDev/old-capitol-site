package com.ivnrdev.connectodo.Domain;


import com.ivnrdev.connectodo.Enums.DepartmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.ivnrdev.connectodo.Enums.EmployeeEnums.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String contactNumber;
    private String address;
    private RoleType role;
    private EmployeeStatus status;
    private DepartmentType department;
    private EmploymentType employment;
    private String username;
    private String password;
    private String profileUrl;
    private String resumeUrl;
    private String nbiClearanceUrl;
    private String nbiClearanceExpiration;
    private ApplicationType applicationType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
