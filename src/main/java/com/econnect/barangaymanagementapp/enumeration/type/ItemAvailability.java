package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ItemAvailability {
    PRIVATE("Private"),
    PUBLIC("Public"),
    DELETED("Deleted");
    private final String name;
}
