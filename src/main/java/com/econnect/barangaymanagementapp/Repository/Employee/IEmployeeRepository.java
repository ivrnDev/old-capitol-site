package com.econnect.barangaymanagementapp.Repository.Employee;

import com.econnect.barangaymanagementapp.DTO.EmployeeDTO;
import com.econnect.barangaymanagementapp.Domain.Employee;
import okhttp3.Response;

import java.util.List;

public interface IEmployeeRepository {
    Response createEmployee(Employee employee);

    Employee updateEmployee(Employee employee);

    void deleteEmployee(Employee employee);

    Employee findEmployeeById(int id);

    List<Employee> findAllEmployees();
//    Employee findEmployeeByCredentials(String username, String password);

}
