package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class EmployeeRepository extends BaseRepository<Employee> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.EMPLOYEES.getPath();

    public EmployeeRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createEmployee(Employee employee) {
        return create(apiKey + "/" + employee.getId(), employee);
    }

    public Response updateEmployee(Employee employee) {
        return create(apiKey + "/" + employee.getId(), employee);

    }

    public Boolean deleteEmployeeById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<Employee> findEmployeeById(String employeeId) {
        return findById(apiKey, employeeId, new TypeReference<>() {
        });
    }

    public List<Employee> findAllEmployees() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

//    public Optional<Employee> findEmployeeByCredentials(String username, String password) {
//
//        try {
//            ArrayList<Employee> employees = InMemoryDatabase.getInstance().getList();
//            return employees.stream()
//                    .filter(employee -> employee.getUsername().equals(username) && employee.getAccess().equals(password))
//                    .findFirst();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public List<Employee> findEmployeeByFilter(Predicate<Employee> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public Response updateEmployeeByStatus(String employeeId, StatusType.EmployeeStatus status) {
        return updateBy(apiKey, employeeId, new TypeReference<>() {
        }, employee -> employee.setStatus(status));
    }
}
