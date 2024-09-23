package com.econnect.barangaymanagementapp.Service;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.UserSession;

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
