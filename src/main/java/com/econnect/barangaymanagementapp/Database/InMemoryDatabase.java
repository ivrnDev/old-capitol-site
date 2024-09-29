package com.econnect.barangaymanagementapp.Database;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.Gender;
import com.econnect.barangaymanagementapp.Enumeration.Roles;
import com.econnect.barangaymanagementapp.Enumeration.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class InMemoryDatabase {
    private static final ArrayList<Employee> list = new ArrayList<>();
    private static InMemoryDatabase instance;

    static {
        list.add(new Employee(
                "12345",                           // id
                "John",                            // firstName
                "Doe",                             // lastName
                "Software Engineer",               // position
                "johndoe@example.com",             // email
                "123-456-7890",                    // contactNumber
                "123 Main St, Anytown, USA",       // address
                Gender.MALE,                       // gender
                Roles.HR_MANAGER,                       // role
                "ivanren",                         // username
                "ivanren",                     // access
                Status.EmployeeStatus.ACTIVE,             // status
                Departments.HUMAN_RESOURCES,                    // department
                LocalDateTime.now(),               // createdAt
                LocalDateTime.now(),               // updatedAt
                LocalDateTime.now().minusDays(1)   // lastLogin
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

