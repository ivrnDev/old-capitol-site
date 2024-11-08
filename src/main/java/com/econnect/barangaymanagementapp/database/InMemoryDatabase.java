package com.econnect.barangaymanagementapp.database;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class InMemoryDatabase {
    private static final ArrayList<Employee> list = new ArrayList<>();
    private static InMemoryDatabase instance;

    static {
        list.add(Employee.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .contactNumber("123-456-7890")
                .address("123 Street, City")
                .role(RoleType.ADMINISTRATIVE_CLERK)
                .department(DepartmentType.BARANGAY_OFFICE)
                .status(StatusType.EmployeeStatus.ACTIVE)
                .username(" ")
                .password(" ")
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .lastLogin(ZonedDateTime.now())
                .build());
    }

    private InMemoryDatabase() {
    }

    public static InMemoryDatabase getInstance() {
        if (instance == null) {
            instance = new InMemoryDatabase();
        }
        return instance;
    }

    public ArrayList<Employee> getList() {
        return list;
    }


}

