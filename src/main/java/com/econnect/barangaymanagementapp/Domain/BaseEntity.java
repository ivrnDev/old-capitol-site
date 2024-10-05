package com.econnect.barangaymanagementapp.Domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
@ToString
@NoArgsConstructor
public abstract class BaseEntity {
    private String id;
}
