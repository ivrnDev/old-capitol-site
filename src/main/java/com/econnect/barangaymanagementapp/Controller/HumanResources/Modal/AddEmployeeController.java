package com.econnect.barangaymanagementapp.Controller.HumanResources.Modal;

import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.Service.EmployeeService;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class AddEmployeeController {
    private final ModalUtils modalUtils;
    private final EmployeeService employeeService;

    public AddEmployeeController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.employeeService = dependencyInjector.getEmployeeService();
    }

    @FXML
    private ImageView closeBtn;

    @FXML
    private TextField residentIdInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField middleNameInput;

    @FXML
    private TextField addressInput;

    @FXML
    private TextField birthdateInput;

    @FXML
    private RadioButton maleBtn;

    @FXML
    private RadioButton femaleBtn;

    @FXML
    private ComboBox<String> volunteeComboBox;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField phoneInput;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button confirmBtn;


    public void initialize() {
        cancelBtn.setOnAction(_ -> closeWindow());

        confirmBtn.setOnAction(_ -> addEmployee());
    }

    private void addEmployee() {
        closeWindow();
//        var employee = new Employee(
//                "909090",                           // id
//                "John",                            // firstName
//                "Doe",                             // lastName
//                "Software Engineer",               // position
//                "johndoe@example.com",             // email
//                "123-456-7890",                    // contactNumber
//                "123 Main St, Anytown, USA",       // address
//                Gender.MALE,                       // gender
//                Roles.HR_MANAGER,                       // role
//                " ",                         // username
//                " ",                     // access
//                Status.EmployeeStatus.ACTIVE,             // status
//                Departments.HUMAN_RESOURCES,                    // department
//                LocalDateTime.now(),               // createdAt
//                LocalDateTime.now(),               // updatedAt
//                LocalDateTime.now().minusDays(1)   // lastLogin
//        );

//        Response response = employeeService.createEmployee(employee);
//        if (response.isSuccessful()) {
//            modalUtils.showModal(Modal.SUCCESS, "Success", "Employee added successfully");
//        }
        modalUtils.showModal(Modal.SUCCESS, "Success", "Employee added successfully");
    }

    @FXML
    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();

        });
    }

    public void closeWindow() {
        modalUtils.closeCustomizeModal();
    }
}