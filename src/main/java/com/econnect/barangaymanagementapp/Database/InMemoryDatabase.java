package com.econnect.barangaymanagementapp.Database;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.Roles;
import com.econnect.barangaymanagementapp.Enumeration.Status;

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
                .role(Roles.HR_MANAGER)
                .department(Departments.HUMAN_RESOURCES)
                .status(Status.EmployeeStatus.ACTIVE)
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

