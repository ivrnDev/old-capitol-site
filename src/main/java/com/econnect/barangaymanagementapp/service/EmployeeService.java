package com.econnect.barangaymanagementapp.service;

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
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus.*;

public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final Set<StatusType.EmployeeStatus> INACTIVE_STATUSES = Set.of(TERMINATED, RESIGNED, REJECTED, PENDING, EVALUATION);
    private final Set<StatusType.EmployeeStatus> APPLICANTS_STATUSES = Set.of(PENDING, UNDER_REVIEW, EVALUATION);
    private String password;

    public EmployeeService(DependencyInjector dependencyInjector) {
        this.employeeRepository = dependencyInjector.getEmployeeRepository();
        this.emailService = dependencyInjector.getEmailService();
    }

    public Response createEmployee(Employee employee) {
        return employeeRepository.createEmployee(employee);
    }

    public Optional<Employee> findEmployeeById(String employeeId) {
        return employeeRepository.findEmployeeById(employeeId);
    }

    public Optional<Employee> findEmployeeByCredentials(String username, String password) {
        if (username.equals("admin") && password.equals("admin")) {
            return Optional.ofNullable(Employee.builder()
                    .firstName("Admin")
                    .lastName("Admin")
                    .department(DepartmentType.BARANGAY_OFFICE)
                    .role(RoleType.ADMIN)
                    .password("admin")
                    .status(ACTIVE)
                    .username("admin")
                    .build());
        }
        return employeeRepository.findEmployeeByFilter(user -> user.getStatus().equals(ACTIVE) && user.getUsername().equals(username) && user.getPassword().equals(password))
                .stream()
                .findFirst();
    }

    public List<Employee> findAllActiveEmployees() {
        return employeeRepository.findEmployeeByFilter(employee -> employee.getStatus().equals(ACTIVE));
    }

    public List<Employee> findAllApplicants() {
        return employeeRepository.findEmployeeByFilter(employee ->
                APPLICANTS_STATUSES.contains(employee.getStatus()));
    }

    public int countAllActiveEmployees() {
        return findAllActiveEmployees().size();
    }

    public Response updateEmployeeToEvaluation(String employeeId) {
        sendEvaluationEmail(findEmployeeById(employeeId).get());
        return updateEmployeeByStatus(employeeId, EVALUATION);
    }

    public Response activateEmployee(String employeeId, DepartmentType department, RoleType role) {
        Optional<Employee> response = findEmployeeById(employeeId);
        if (response.isPresent()) {
            try {
                Employee employee = generateEmployeeUsernameAndPassword(response.get());
                employee.setStatus(ACTIVE);
                employee.setDepartment(department);
                employee.setRole(role);
                sendActivationEmail(employee, department, role);
                return employeeRepository.updateEmployee(employee);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Response updateEmployeeToUnderReview(String employeeId) {
        return updateEmployeeByStatus(employeeId, UNDER_REVIEW);
    }

    public Response terminateEmployee(String employeeId) {
        return updateEmployeeByStatus(employeeId, TERMINATED);
    }

    public Response rejectEmployee(String employeeId) {
        Optional<Employee> findEmployee = findEmployeeById(employeeId);
        if (!findEmployee.isPresent()) return null;
        Employee employee = findEmployee.get();
        Response response = updateEmployeeByStatus(employeeId, REJECTED);
        if (response.isSuccessful()) {
            sendRejectionEmail(employee);
        }
        return response;
    }

    public Response updateEmployeeByStatus(String employeeId, StatusType.EmployeeStatus status) {
        return employeeRepository.updateEmployeeByStatus(employeeId, status);
    }

    private Employee generateEmployeeUsernameAndPassword(Employee employee) {
        int employeeCount = countAllActiveEmployees() + 1;
        String formattedId = String.format("%03d", employeeCount);
        String sanitizedLastName = employee.getLastName().toLowerCase().replaceAll("\\s+", "");
        String username = sanitizedLastName + formattedId;
        password = sanitizedLastName + generate6DigitPin();
        employee.setUsername(username);
        employee.setPassword(password);

        return employee;
    }

    private int generate6DigitPin() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    //Update Listener
    public void enableLiveReload(Consumer<String> handleDataUpdate) {
        employeeRepository.enableLiveReload(handleDataUpdate);
    }

    private void sendActivationEmail(Employee employee, DepartmentType department, RoleType role) {
        emailService.sendEmailAsync(employee.getEmail(), "Congratulations! You Have Been Hired", String.format("""
                        Dear %s,

                        We are thrilled to inform you that you have successfully completed our hiring process and have been officially hired to join our team.
                                                    
                        Welcome aboard! We are excited to have you as part of our growing team.                            
                        Your assigned role will be as %s in the %s department. We believe your skills and experiences will be a valuable asset to the team.
                                                    
                        Please report to our office at Old Capitol Site to finalize the onboarding process. If you have any questions, feel free to reach out to our HR team.
                                                        
                        Please do not share this username and password to anyone.
                        Username: %s
                        Password: %s
                                                    
                        Best regards,
                        Old Capitol Site
                        """,
                employee.getFirstName(),
                role.getName(),
                department.getName(),
                employee.getUsername(),
                password
        ));
    }

    private void sendEvaluationEmail(Employee employee) {
        try {
            emailService.sendEmailAsync(employee.getEmail(), "Congratulations! Your Application has been accepted", String.format("""
                            Dear %s,

                                We are writing to inform you that your application has been carefully reviewed and we are pleased to announce that it has been accepted for further consideration.
                                                            
                            To proceed with the next step, we kindly request you to visit our office located at Old Capitol Site to process you interview.
                                                            
                            We look forward to meeting you and learning more about your qualifications.
                                            
                            Best regards,
                            Old Capitol Site                
                            """,
                    employee.getFirstName()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRejectionEmail(Employee employee) {
        emailService.sendEmailAsync(employee.getEmail(), "We're sorry, Your Application is rejected", String.format("""
                        Dear %s,
                            We regret to inform you that your application will not be moving forward at this time.
                                                                     
                        We appreciate your interest in joining our team and encourage you to reapply for future opportunities that align with your skills and experiences.
                                                                     
                        Best regards,
                        Old Capitol Site
                                 """,
                employee.getFirstName()
        ));
    }
}
