package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.type.EmploymentType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.repository.EmployeeRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus.*;

public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final Set<StatusType.EmployeeStatus> INACTIVE_STATUSES = Set.of(TERMINATED, INACTIVE, RESIGNED, RETIRED, REJECTED, PENDING, EVALUATION);
    private final Set<StatusType.EmployeeStatus> APPLICANTS_STATUSES = Set.of(PENDING, EVALUATION);

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

    public List<Employee> findAllActiveEmployees() {
        return employeeRepository.findEmployeeByFilter(employee ->
                !INACTIVE_STATUSES.contains(employee.getStatus()));
    }

    public List<Employee> findAllApplicants() {
        return employeeRepository.findEmployeeByFilter(employee -> APPLICANTS_STATUSES.contains(employee.getStatus()));
    }

    public Response updateEmployeeByStatus(String employeeId, StatusType.EmployeeStatus status) {
        return employeeRepository.updateEmployeeByStatus(employeeId, status);
    }

}
