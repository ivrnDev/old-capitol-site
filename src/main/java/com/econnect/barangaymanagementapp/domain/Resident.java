package com.econnect.barangaymanagementapp.domain;

import com.econnect.barangaymanagementapp.config.deserializer.GenericSerializer;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonSerialize(using = GenericSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Resident extends BaseEntity {
    private String firstName;
    private String middleName;
    private String lastName;
    private String nameExtension;
    private String mobileNumber;
    private String telephoneNumber;
    private String email;
    private String address;
    private GenderType sex;
    private String birthdate;
    private String age;
    private String birthplace;
    private String citizenship;
    private String occupation;
    private CivilStatus civilStatus;
    private String motherTounge;
    private BloodType bloodType;
    private String religion;

    private String fatherFirstName;
    private String fatherLastName;
    private String fatherMiddleName;
    private String fatherSuffixName;
    private String fatherOccupation;

    private String motherFirstName;
    private String motherLastName;
    private String motherMiddleName;
    private String motherSuffixName;
    private String motherOccupation;

    private String spouseFirstName;
    private String spouseLastName;
    private String spouseMiddleName;
    private String spouseSuffixName;
    private String spouseOccupation;

    private String emergencyFirstName;
    private String emergencyLastName;
    private String emergencyMiddleName;
    private String emergencyMobileNumber;
    private String emergencyRelationship;

    private String sourceOfIncome;
    private ResidencyStatus residencyStatus;
    private ResidentStatus status;
    private String educationalAttainment;
    private String profileUrl;
    private String validIdUrl;
    private String validIdExpiration;
    private String tinIdUrl;
    private String tinIdNumber;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
