package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Domain.Employee;

public class UserSession {
    private static UserSession instance;
    private Employee currentEmployee;

    private UserSession() {}

    public static UserSession getInstance() {
        if(instance == null) {
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
    public void clearSession() {
       currentEmployee = null;
    }


}
