package com.econnect.barangaymanagementapp.Controller.HumanResources;

import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Utils.ButtonUtils;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    @FXML
    private VBox content;

    @FXML
    private Button addEmployeeBtn;

    private final ButtonUtils buttonUtils;
    private final EmployeeService employeeService;


    public EmployeeController(DependencyInjector dependencyInjector) {
        this.buttonUtils = dependencyInjector.getButtonUtils();
        this.employeeService = dependencyInjector.getEmployeeService();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addEmployeeBtn.setOnAction(e -> {
//            Employee newEmployee = new Employee(
//                    "12345",                           // id
//                    "Ivan Ren",                            // firstName
//                    "Villamora",                             // lastName
//                    "Secretary",               // position
//                    "johndoe@example.com",             // email
//                    "123-456-7890",                    // contactNumber
//                    "123 Main St, Anytown, USA",       // address
//                    Gender.MALE,                       // gender
//                    Roles.HR_MANAGER,                       // role
//                    "ivanren",                         // username
//                    "ivanren",                     // access
//                    Status.EmployeeStatus.ACTIVE,             // status
//                    Departments.HUMAN_RESOURCES,                    // department
//                    LocalDateTime.now(),               // createdAt
//                    LocalDateTime.now(),               // updatedAt
//                    LocalDateTime.now().minusDays(1)   // lastLogin
//            );
//
//            Response created = employeeService.createEmployee(newEmployee);
//
//            System.out.println("Employee created: " + created);

//            for (EmployeeDTO employee : employees) {
//                System.out.println(employee);
//            }
        });


    }
}