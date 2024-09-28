package com.econnect.barangaymanagementapp.Database;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.Gender;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class InMemoryDatabase {
    private static final ArrayList<Employee> list = new ArrayList<>();
    private static InMemoryDatabase instance;

    static {
        list.add(new Employee(
                "E001",
                "John",
                "Doe",
                "Software Engineer",
                "Active",
                "john.doe@example.com",
                "123-456-7890",
                "123 Main St, New York, NY",
                Gender.MALE,
                "ivanren",
                "ivanren",
                "Employee",
                Departments.HUMAN_RESOURCES,
                LocalDateTime.of(2022, 1, 15, 9, 0),
                LocalDateTime.of(2023, 1, 25, 10, 30),
                LocalDateTime.of(2023, 9, 15, 8, 45)
        ));
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

