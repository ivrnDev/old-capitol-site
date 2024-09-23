package com.econnect.barangaymanagementapp.Service;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Repository.Employee.EmployeeRepository;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;

import java.util.Optional;

public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(DependencyInjector dependencyInjector) {
        this.employeeRepository = dependencyInjector.getEmployeeRepository();
    }

    public Optional<Employee> findEmployeeByCredentials(String username, String password) {
        return employeeRepository.findEmployeeByCredentials(username, password);
    }
//    public Employee createEmployee(Employee employee) {
//        return employeeRepository.createEmployee(employee);
//    }
}
