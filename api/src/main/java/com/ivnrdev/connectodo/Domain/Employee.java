package com.ivnrdev.connectodo.Domain;


import com.ivnrdev.connectodo.Enums.DepartmentType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

import static com.ivnrdev.connectodo.Enums.EmployeeEnums.*;

@Data
@Builder
@Table("employees")
public class Employee {
    @Id
    private Long id;
    private Long residentId;

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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
