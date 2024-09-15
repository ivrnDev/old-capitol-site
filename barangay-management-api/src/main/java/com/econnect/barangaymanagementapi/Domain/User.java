package com.econnect.barangaymanagementapi.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;

}
