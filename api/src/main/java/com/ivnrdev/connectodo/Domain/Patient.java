package com.ivnrdev.connectodo.Domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("patient")
public class Patient {
    private long id;
    private String address;
    private String age;
    private String birthdate;
    private String civilStatus;
    private String mobileNumber;
    private String name;
    private String sex;
    private String status;
}
