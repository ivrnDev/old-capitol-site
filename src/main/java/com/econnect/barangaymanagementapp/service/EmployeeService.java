package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.database.InMemoryDatabase;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.repository.EmployeeRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.data.PasswordUtils;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus.*;

public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordUtils passwordUtils;
    private final Set<StatusType.EmployeeStatus> INACTIVE_STATUSES = Set.of(TERMINATED, INACTIVE, RESIGNED, RETIRED, REJECTED, PENDING, EVALUATION);
    private final Set<StatusType.EmployeeStatus> APPLICANTS_STATUSES = Set.of(PENDING, UNDER_REVIEW, EVALUATION);

    public EmployeeService(DependencyInjector dependencyInjector) {
        this.employeeRepository = dependencyInjector.getEmployeeRepository();
        this.passwordUtils = dependencyInjector.getPasswordUtils();
    }

    public Response createEmployee(Employee employee) {
        return employeeRepository.createEmployee(employee);
    }

    public List<Employee> findAllEmployees() {
        return employeeRepository.findAllEmployees();
    }

    public Optional<Employee> findEmployeeByCredentials(String username, String password) {
//        if (countAllActiveEmployees() == 0 && username.equals("admin") && password.equals("admin")) {
//            return Optional.ofNullable(Employee.builder()
//                    .firstName("Admin")
//                    .lastName("Admin")
//                    .department(DepartmentType.HUMAN_RESOURCES)
//                    .access(passwordUtils.encryptPassword("admin"))
//                    .status(ACTIVE)
//                    .username("admin")
//                    .build());
//        }
//        return employeeRepository.findEmployeeByFilter(user -> user.getUsername().equals(username) && passwordUtils.comparePassword(password, user.getAccess()))
//                .stream()
//                .findFirst();
        var employee = InMemoryDatabase.getInstance().getList();
        return employee.stream()
                .filter(user -> user.getUsername().equals(username) && user.getAccess().equals(password))
                .findFirst();
    }

    public List<Employee> findAllEmployeesByStatus(StatusType.EmployeeStatus status) {
        return employeeRepository.findEmployeeByFilter(employee -> employee.getStatus().equals(status));
    }

    public List<Employee> findAllActiveEmployees() {
        return employeeRepository.findEmployeeByFilter(employee ->
                !INACTIVE_STATUSES.contains(employee.getStatus()));
    }

    public List<Employee> findAllOnlineApplicants() {
        return employeeRepository.findEmployeeByFilter(employee ->
                APPLICANTS_STATUSES.contains(employee.getStatus()) && employee.getApplicationType().equals(ApplicationType.ONLINE));
    }

    public List<Employee> findAllApplicants() {
        return employeeRepository.findEmployeeByFilter(employee ->
                APPLICANTS_STATUSES.contains(employee.getStatus()));
    }

    public int countAllActiveEmployees() {
        return findAllActiveEmployees().size();
    }

    public Response activateEmployee(String employeeId, DepartmentType departmentType, RoleType role) {
        return updateEmployeeByStatusDepartmentRole(employeeId, ACTIVE, departmentType, role);
    }

    public Response deactivateEmployee(String employeeId) {
        return updateEmployeeByStatus(employeeId, INACTIVE);
    }

    public Response terminateEmployee(String employeeId) {
        return updateEmployeeByStatus(employeeId, TERMINATED);
    }

    public Response rejectEmployee(String employeeId) {
        return updateEmployeeByStatus(employeeId, REJECTED);
    }

    public Response updateEmployeeToEvaluation(String employeeId, String clearanceLink, String expirationDate) {
        Optional<Employee> employee = employeeRepository.findEmployeeById(employeeId);

        if (employee.isPresent()) {
            Employee updatedEmployee = employee.get();
            updatedEmployee.setNbiClearanceUrl(clearanceLink);
            updatedEmployee.setNbiClearanceExpiration(expirationDate);
            updatedEmployee.setStatus(EVALUATION);
            return employeeRepository.updateEmployee(updatedEmployee);
        }
        return null;
    }

    private Response updateEmployeeByStatusDepartmentRole(String employeeId, StatusType.EmployeeStatus status, DepartmentType department, RoleType role) {
        Optional<Employee> employee = employeeRepository.findEmployeeById(employeeId);

        if (employee.isPresent()) {
            Employee updatedEmployee = generateEmployeeUsernameAndPassword(employee.get());
            updatedEmployee.setStatus(status);
            updatedEmployee.setDepartment(department);
            updatedEmployee.setRole(role);
            return employeeRepository.updateEmployee(updatedEmployee);
        }
        return employeeRepository.updateEmployeeByStatus(employeeId, status);
    }

    private Response updateEmployeeByStatus(String employeeId, StatusType.EmployeeStatus status) {
        if (!status.equals(ACTIVE)) {
            return employeeRepository.updateEmployeeByStatus(employeeId, status);
        }
        return null;
    }

    private Employee generateEmployeeUsernameAndPassword(Employee employee) {
        int employeeCount = countAllActiveEmployees() + 1;
        String formattedId = String.format("%03d", employeeCount);
        String username = employee.getLastName().toLowerCase() + formattedId;
        String password = employee.getLastName().toLowerCase() + formattedId;
        String hashedPassword = passwordUtils.encryptPassword(password);
        employee.setUsername(username);
        employee.setAccess(hashedPassword);
        return employee;
    }
}