package com.econnect.barangaymanagementapp.util.state;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.type.DepartmentType;
import com.econnect.barangaymanagementapp.enumeration.type.RoleType;
import javafx.scene.image.Image;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class UserSession {
    private static UserSession instance;
    private Employee currentEmployee;
    @Setter
    private Image employeeImage;

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setSession(Employee employee) {
        this.currentEmployee = employee;
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
