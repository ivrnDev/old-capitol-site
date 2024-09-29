package com.econnect.barangaymanagementapp.Controller.HumanResources.Modal;

import com.econnect.barangaymanagementapp.Enumeration.Modal;
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

    public AddEmployeeController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
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

        confirmBtn.setOnAction(_ -> closeWindow());
    }

    @FXML
    private void closeWindowConfirmation() {
        modalUtils.showModal(Modal.DEFAULT_REJECT, "Confirm Exit", "Are you sure you want to exit this window? Any unsaved changes will be lost.", result -> {
            if (result) closeWindow();

        });
    }

    public void closeWindow() {
        modalUtils.closeModal();
    }
}