package com.econnect.barangaymanagementapp.Database;

import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Enumeration.Departments;

import java.util.ArrayList;

public class InMemoryDatabase {
    private static final ArrayList<Employee> list = new ArrayList<>();
    private static InMemoryDatabase instance;

    static {
        list.add(new Employee("22-0214", "Laiza", "De Castro", "laiza", "laiza", "admin",Departments.HUMAN_RESOURCE));
        list.add(new Employee("22-0215", "Ivan Ren", "Villamora", "ivanren", "ivanren", "admin", Departments.BARANGAY_OFFICE));
    }

    private InMemoryDatabase() {}

    public static InMemoryDatabase getInstance() {
        if(instance == null) {
            instance = new InMemoryDatabase();
        }
        return instance;
    }

    public ArrayList<Employee> getList() {
        return list;
    }



}

