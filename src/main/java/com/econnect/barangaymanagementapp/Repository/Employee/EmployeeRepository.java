package com.econnect.barangaymanagementapp.Repository.Employee;

import com.econnect.barangaymanagementapp.Database.InMemoryDatabase;
import com.econnect.barangaymanagementapp.Domain.Employee;

import java.util.ArrayList;
import java.util.Optional;

public class EmployeeRepository implements IEmployeeRepository {

    public Optional<Employee> findEmployeeByCredentials(String username, String password) {
        ArrayList<Employee> employees = InMemoryDatabase.getInstance().getList();
        return employees.stream()
                .filter(employee -> employee.getUsername().equals(username) && employee.getPassword().equals(password))
                .findFirst();
    }
}
