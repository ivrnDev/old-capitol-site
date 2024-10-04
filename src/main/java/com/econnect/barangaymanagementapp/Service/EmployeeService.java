package com.econnect.barangaymanagementapp.Service;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Repository.EmployeeRepository;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.PasswordUtils;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;

public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(DependencyInjector dependencyInjector) {
        this.employeeRepository = dependencyInjector.getEmployeeRepository();
    }

    public Optional<Employee> findEmployeeByCredentials(String username, String password) {
        return employeeRepository.findEmployeeByCredentials(username, password);
    }

    public Response createEmployee(Employee employee) {
        return employeeRepository.createEmployee(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAllEmployees();
    }

}
