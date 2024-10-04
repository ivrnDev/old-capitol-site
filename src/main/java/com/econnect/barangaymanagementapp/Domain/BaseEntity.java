package com.econnect.barangaymanagementapp.Domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity {
    private String id;
}
