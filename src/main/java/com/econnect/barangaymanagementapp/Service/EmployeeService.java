package com.econnect.barangaymanagementapp.Service;

import com.econnect.barangaymanagementapp.DTO.EmployeeDTO;
import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Mapper.EmployeeMapper;
import com.econnect.barangaymanagementapp.Repository.Employee.EmployeeRepository;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.PasswordUtils;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordUtils passwordUtils;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(DependencyInjector dependencyInjector) {
        this.employeeRepository = dependencyInjector.getEmployeeRepository();
        this.passwordUtils = dependencyInjector.getPasswordEncryption();
        this.employeeMapper = dependencyInjector.getEmployeeMapper();
    }

    public Optional<Employee> findEmployeeByCredentials(String username, String password) {
        return employeeRepository.findEmployeeByCredentials(username, password);
    }

    public Response createEmployee(Employee employee) {
        employee.setAccess(passwordUtils.encryptPassword(employee.getAccess())); // Encrypt password
        return employeeRepository.createEmployee(employee);
    }

    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAllEmployees();
        return employees.stream()
                .map(employee -> employeeMapper.toDto(employee))
                .collect(Collectors.toList());
    }

}
