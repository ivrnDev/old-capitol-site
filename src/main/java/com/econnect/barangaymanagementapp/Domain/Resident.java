package com.econnect.barangaymanagementapp.Domain;

import com.econnect.barangaymanagementapp.Config.Deserializer.GenderDeserializer;
import com.econnect.barangaymanagementapp.Enumeration.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Resident extends BaseEntity {
    @JsonProperty("AEmail")
    private String email;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("Age")
    private String age;

    @JsonProperty("Birthdate")
    private String birthdate;

    @JsonProperty("CapabToTravel")
    private String capabToTravel;

    @JsonProperty("Contact")
    private String contact;

    @JsonProperty("EducBG")
    private String educBG;

    @JsonProperty("EmployBusiness")
    private String employBusiness;

    @JsonProperty("Evader")
    private String evader;

    @JsonProperty("FatherFirstName")
    private String fatherFirstName;

    @JsonProperty("FatherLastName")
    private String fatherLastName;

    @JsonProperty("FatherMiddleName")
    private String fatherMiddleName;

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("GOVTID")
    private String govtID;

    @JsonProperty("ICA")
    private String ica;

    @JsonProperty("Image1x1URL")
    private String image1x1URL;

    @JsonProperty("LanguageSpoken")
    private String languageSpoken;

    @JsonProperty("LastName")
    private String lastName;

    @JsonProperty("MiddleName")
    private String middleName;

    @JsonProperty("MotherFirstName")
    private String motherFirstName;

    @JsonProperty("MotherLastName")
    private String motherLastName;

    @JsonProperty("MotherMiddleName")
    private String motherMiddleName;

    @JsonProperty("NameExtension")
    private String nameExtension;

    @JsonProperty("OSCA")
    private String osca;

    @JsonProperty("PMR")
    private String pmr;

    @JsonProperty("PhilHealth")
    private String philHealth;

    @JsonProperty("RIP")
    private String rip;

    @JsonProperty("SCA")
    private String sca;

    @JsonProperty("SSS")
    private String sss;

    @JsonProperty("Sex")
    @JsonDeserialize(using = GenderDeserializer.class)
    private Gender gender;

    @JsonProperty("SourceIncome")
    private String sourceIncome;

    @JsonProperty("Specialization")
    private String specialization;

    @JsonProperty("SpouseFirstName")
    private String spouseFirstName;

    @JsonProperty("SpouseLastName")
    private String spouseLastName;

    @JsonProperty("SpouseMiddleName")
    private String spouseMiddleName;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("TIN")
    private String tin;

    @JsonProperty("ValidIDURL")
    private String validIDURL;
}
