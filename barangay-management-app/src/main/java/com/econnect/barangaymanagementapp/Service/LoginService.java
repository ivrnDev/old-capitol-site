package com.econnect.barangaymanagementapp.Service;

public class LoginService {
    public boolean login(String username, String password) {
        return "hr".equals(username) && "hr".equals(password);
    }
}
