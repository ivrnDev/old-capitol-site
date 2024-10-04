package com.econnect.barangaymanagementapp.Domain;

import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.Gender;
import com.econnect.barangaymanagementapp.Enumeration.Roles;

import java.time.LocalDateTime;

import static com.econnect.barangaymanagementapp.Enumeration.Status.EmployeeStatus;

public class Employee extends BaseEntity {
    private String firstName;
    private String lastName;
    private String position;
    private String email;
    private String contactNumber;
    private String address;
    private Gender gender;
    private Roles role;
    private EmployeeStatus status;
    private Departments department;
    private String username;
    private String access;
    private String profilePictureUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    private Employee(Builder builder) {
        super(builder.id); // Initialize BaseEntity's ID
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.position = builder.position;
        this.email = builder.email;
        this.contactNumber = builder.contactNumber;
        this.address = builder.address;
        this.gender = builder.gender;
        this.role = builder.role;
        this.status = builder.status;
        this.department = builder.department;
        this.username = builder.username;
        this.access = builder.access;
        this.profilePictureUrl = builder.profilePictureUrl;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.lastLogin = builder.lastLogin;
    }

    public static class Builder {
        private String id;
        private String firstName;
        private String lastName;
        private String position;
        private String email;
        private String contactNumber;
        private String address;
        private Gender gender;
        private Roles role;
        private EmployeeStatus status;
        private Departments department;
        private String username;
        private String access;
        private String profilePictureUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLogin;

        public Builder(String id) {
            this.id = id;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder contactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder role(Roles role) {
            this.role = role;
            return this;
        }

        public Builder status(EmployeeStatus status) {
            this.status = status;
            return this;
        }

        public Builder department(Departments department) {
            this.department = department;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder access(String access) {
            this.access = access;
            return this;
        }

        public Builder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder lastLogin(LocalDateTime lastLogin) {
            this.lastLogin = lastLogin;
            return this;
        }

        public Employee build() {
            // Perform validations if needed before constructing Employee
            if (department != null && !department.getRoles().contains(role)) {
                throw new IllegalArgumentException("The role " + role + " is not valid for department " + department.getName());
            }
            return new Employee(this);
        }
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public void setDepartment(Departments department) {
        this.department = department;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPosition() {
        return position;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public Gender getGender() {
        return gender;
    }

    public Roles getRole() {
        return role;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public Departments getDepartment() {
        return department;
    }

    public String getUsername() {
        return username;
    }

    public String getAccess() {
        return access;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + super.getId() + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", position='" + position + '\'' +
                ", email='" + email + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", address='" + address + '\'' +
                ", gender=" + gender +
                ", role=" + role +
                ", username='" + username + '\'' +
                ", access='" + access + '\'' +
                ", status=" + status +
                ", department=" + department +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
