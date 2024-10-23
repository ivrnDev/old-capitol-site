package com.econnect.barangaymanagementapp.domain;

import com.econnect.barangaymanagementapp.config.deserializer.JacksonFactory;
import com.econnect.barangaymanagementapp.config.deserializer.GenericSerializer;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonSerialize(using = GenericSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resident extends BaseEntity {
    private String firstName;
    private String middleName;
    private String lastName;
    private String nameExtension;
    private String contactNumber;
    private String email;
    private String address;
    private GenderType sex;
    private String birthdate;
    private String age;
    private String birthplace;
    private String citizenship;
    private String occupation;
    private CivilStatus civilStatus;
    private MotherTongue motherTounge;
    private BloodType bloodType;
    private Religion religion;

    private String fatherFirstName;
    private String fatherLastName;
    private String fatherMiddleName;
    private String fatherSuffixName;
    private String fatherOccupation;
    private String fatherBirthdate;

    private String motherFirstName;
    private String motherLastName;
    private String motherMiddleName;
    private String motherSuffixName;
    private String motherOccupation;
    private String motherBirthdate;

    private String spouseFirstName;
    private String spouseLastName;
    private String spouseMiddleName;
    private String spouseSuffixName;
    private String spouseOccupation;
    private String spouseBirthdate;

    private String houseHoldIncome;
    private EconomicLevelType economicLevel;
    private ResidentStatus status;
    private String profileUrl;
    private String validIdURL;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
