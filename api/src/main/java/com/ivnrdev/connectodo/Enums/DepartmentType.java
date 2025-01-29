package com.ivnrdev.connectodo.Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DepartmentType {
    NONE("None"),
    OFFICE("Office"),
    HEALTH("Health"),
    LUPON("Lupon");

    private final String name;
}
