package com.econnect.barangaymanagementapp.domain;

import com.econnect.barangaymanagementapp.config.deserializer.GenderDeserializer;
import com.econnect.barangaymanagementapp.enumeration.type.GenderType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resident extends BaseEntity {
    private String firstName;
    private String middleName;
    private String lastName;
    private String nameExtension;
    private String contactNumber;
    private String email;
    private String address;
    @JsonDeserialize(using = GenderDeserializer.class)
    private GenderType sex;
    private String age;
    private String birthdate;
    private String birthplace;
    private String citizenship;
    private String civilStatus;
    private MotherTongue motherTounge;
    private BloodType bloodType;
    private Religion religion;
    private String occupation;

    private String fatherFirstName;
    private String fatherLastName;
    private String fatherMiddleName;
    private String fatherNameExtension;
    private String fatherOccupation;
    private String fatherBirthdate;

    private String motherFirstName;
    private String motherLastName;
    private String motherMiddleName;
    private String motherNameExtension;
    private String motherOccupation;

    private String spouseFirstName;
    private String spouseLastName;
    private String spouseMiddleName;
    private String spouseNameExtension;
    private String spouseOccupation;

    private EconomicLevelType houseHoldIncome;
    private ResidentStatus status;
    private String profileUrl;
    private String validIdURL;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
