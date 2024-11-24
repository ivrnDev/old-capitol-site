package com.econnect.barangaymanagementapp.enumeration.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Itemtype {
    HEALTH_SUPPLIES("Health Supplies"),
    TOOLS_AND_EQUIPMENT("Tools and Equipment"),
    OFFICE_SUPPLIES("Office Supplies"),
    EVENT_SUPPLIES("Event Supplies");
    private final String name;

    public static Itemtype fromName(String requestType) {
        for (Itemtype type : Itemtype.values()) {
            if (type.getName().equalsIgnoreCase(requestType)) {
                return type;
            }
        }
        return null;
    }
}
