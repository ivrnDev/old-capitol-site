package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.type.EmploymentType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.repository.EmployeeRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
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

    public List<Employee> findAllEmployees() {
        return employeeRepository.findAllEmployees();
    }

    public List<Employee> findAllEmployeesByStatus(StatusType.EmployeeStatus status) {
        return employeeRepository.findEmployeeByFilter(employee -> employee.getStatus().equals(status));
    }

    public List<Employee> findAllEmployeesStatusExcept(StatusType.EmployeeStatus status) {
        return employeeRepository.findEmployeeByFilter(employee -> !employee.getStatus().equals(status));
    }

    public List<Employee> findAllEmployeesByEmployment(EmploymentType type) {
        return employeeRepository.findEmployeeByFilter(employee -> !employee.getEmployment().equals(type));
    }

    public Response updateEmployeeByStatus(String employeeId, StatusType.EmployeeStatus status) {
        return employeeRepository.updateEmployeeByStatus(employeeId, status);
    }

}
