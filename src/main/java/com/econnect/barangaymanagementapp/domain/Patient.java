package com.econnect.barangaymanagementapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Patient extends BaseEntity {
    private String address;
    private String age;
    private String birthdate;
    private String civilStatus;
    private String mobileNumber;
    private String name;
    private String sex;
    private String status;
}
