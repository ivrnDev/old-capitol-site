package com.econnect.barangaymanagementapp.Domain;

import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.EmploymentType;
import com.econnect.barangaymanagementapp.Enumeration.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static com.econnect.barangaymanagementapp.Enumeration.Status.EmployeeStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Employee extends BaseEntity {
    private String firstName;
    private String lastName;
    private String middleName;
    private String position;
    private String email;
    private String contactNumber;
    private String address;
    private Roles role;
    private EmployeeStatus status;
    private Departments department;
    private EmploymentType employment;
    private String username;
    private String access;
    private String profilePictureUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
}
