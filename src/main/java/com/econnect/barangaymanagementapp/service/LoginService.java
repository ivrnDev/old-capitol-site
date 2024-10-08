package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.state.UserSession;

import java.util.Optional;

public class LoginService {
    private final EmployeeService employeeService;
    private final UserSession userSession;

    public LoginService(DependencyInjector dependencyInjector) {
        this.employeeService = dependencyInjector.getEmployeeService();
        this.userSession = dependencyInjector.getUserSession();
    }

    public Optional<Employee> login(String username, String password) {
        Optional<Employee> employee = employeeService.findEmployeeByCredentials(username, password);
        if (employee != null && employee.isPresent()) {
            userSession.setSession(employee.get());
            return employee;
        }
        return Optional.empty();
    }
}
