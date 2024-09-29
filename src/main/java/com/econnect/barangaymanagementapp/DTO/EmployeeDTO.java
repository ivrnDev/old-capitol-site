package com.econnect.barangaymanagementapp.DTO;

import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.Gender;
import com.econnect.barangaymanagementapp.Enumeration.Roles;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

import static com.econnect.barangaymanagementapp.Enumeration.Status.EmployeeStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String position;
    private String email;
    private String contactNumber;
    private String address;
    private Gender gender;
    private Roles role;
    private String username;
    private EmployeeStatus status;
    private Departments department;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    public EmployeeDTO() {
    }

    public EmployeeDTO(String id, String firstName, String lastName, String position, String email, String contactNumber, String address, Gender gender, Roles role, String username, String access, EmployeeStatus status, Departments department, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLogin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.email = email;
        this.contactNumber = contactNumber;
        this.address = address;
        this.gender = gender;
        this.role = role;
        this.username = username;
        this.status = status;
        this.department = department;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLogin = lastLogin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public Departments getDepartment() {
        return department;
    }

    public void setDepartment(Departments department) {
        this.department = department;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", position='" + position + '\'' +
                ", email='" + email + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", address='" + address + '\'' +
                ", gender=" + gender +
                ", role='" + role + '\'' +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", department=" + department +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
