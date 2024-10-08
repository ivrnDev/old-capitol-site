package com.econnect.barangaymanagementapp.util.state;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;

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

    public DepartmentType getEmployeeDepartment() {
        return currentEmployee.getDepartment();
    }

    public RoleType getEmployeeRole() {
        return currentEmployee.getRole();
    }

    public void clearSession() {
        currentEmployee = null;
    }

    public boolean hasSession() {
        return currentEmployee != null;
    }
}
