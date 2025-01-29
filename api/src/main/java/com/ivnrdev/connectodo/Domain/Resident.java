package com.ivnrdev.connectodo.Domain;

import com.ivnrdev.connectodo.Enums.ResidentEnums.BloodType;
import com.ivnrdev.connectodo.Enums.ResidentEnums.CivilStatus;
import com.ivnrdev.connectodo.Enums.ResidentEnums.ResidencyStatus;
import com.ivnrdev.connectodo.Enums.ResidentEnums.ResidentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("Residents")
public class Resident {
    @Id
    private Long id;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
