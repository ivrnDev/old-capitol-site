package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.enumeration.path.FXMLPath;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import javafx.scene.image.Image;

import java.util.Objects;
import java.util.Optional;

import static com.econnect.barangaymanagementapp.enumeration.path.FXMLPath.DEFAULT_PROFILE;

public class LoginService {
    private final EmployeeService employeeService;
    private final UserSession userSession;
    private final ImageService imageService;

    public LoginService(DependencyInjector dependencyInjector) {
        this.employeeService = dependencyInjector.getEmployeeService();
        this.userSession = dependencyInjector.getUserSession();
        this.imageService = dependencyInjector.getImageService();
    }

    public Optional<Employee> login(String username, String password) {
        Optional<Employee> employee = employeeService.findEmployeeByCredentials(username, password);
        if (employee != null && employee.isPresent()) {
            userSession.setSession(employee.get());
            if (employee.get().getProfileUrl() != null) {
                userSession.setEmployeeImage(imageService.getImage(Firestore.PROFILE_PICTURE.getPath(), employee.get().getProfileUrl()));
            } else {
                userSession.setEmployeeImage(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(DEFAULT_PROFILE.getFxmlPath()))));
            }
            return employee;
        }
        return Optional.empty();
    }
}
