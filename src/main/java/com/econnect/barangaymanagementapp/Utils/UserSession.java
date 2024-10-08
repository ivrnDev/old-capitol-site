package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Enumeration.Departments;
import com.econnect.barangaymanagementapp.Enumeration.Roles;

public class UserSession {
    private static UserSession instance;
    private Employee currentEmployee;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setSession(Employee employee) {
        this.currentEmployee = employee;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public Departments getEmployeeDepartment() {
        return currentEmployee.getDepartment();
    }

    public Roles getEmployeeRole() {
        return currentEmployee.getRole();
    }

    public void clearSession() {
        currentEmployee = null;
    }

    public boolean hasSession() {
        return currentEmployee != null;
    }
}
