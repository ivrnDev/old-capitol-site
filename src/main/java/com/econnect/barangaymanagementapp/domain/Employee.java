package com.econnect.barangaymanagementapp.domain;

import com.econnect.barangaymanagementapp.config.deserializer.GenericSerializer;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.EmploymentType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus;

;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@JsonSerialize(using = GenericSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee extends BaseEntity {
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
    private String access;
    private String profileUrl;
    private String resumeUrl;
    private ApplicationType applicationType;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime lastLogin;
}
