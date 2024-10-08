package com.econnect.barangaymanagementapp.database;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;

import java.time.LocalDateTime;
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
                .role(RoleType.HR_MANAGER)
                .department(DepartmentType.HUMAN_RESOURCES)
                .status(StatusType.EmployeeStatus.ACTIVE)
                .username(" ")
                .access(" ")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
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

