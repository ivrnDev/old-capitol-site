package com.ivnrdev.connectodo.DTO.Resident;

import com.ivnrdev.connectodo.Enums.ResidentEnums;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResidentRequestDTO {
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffixName;
    private String mobileNumber;
    private String telephoneNumber;
    private String email;
    private String address;
    private String sex;
    private String birthdate;
    private String birthplace;
    private String citizenship;
    private String occupation;
    private ResidentEnums.CivilStatus civilStatus;
    private String motherTounge;
    private ResidentEnums.BloodType bloodType;
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
    private ResidentEnums.ResidencyStatus residencyStatus;
    private ResidentEnums.ResidentStatus status;
    private String educationalAttainment;
    private String profileUrl;
    private String validIdUrl;
    private String validIdExpiration;
    private String tinIdUrl;
    private String tinIdNumber;
}
