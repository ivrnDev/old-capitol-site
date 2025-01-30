package com.ivnrdev.connectodo.Enums;

import lombok.Getter;

@Getter
public enum DepartmentType {
    NONE("None"),
    OFFICE("Office"),
    HEALTH("Health"),
    LUPON("Lupon");

    private final String name;

    DepartmentType(String name) {
        this.name = name;
    }
}
