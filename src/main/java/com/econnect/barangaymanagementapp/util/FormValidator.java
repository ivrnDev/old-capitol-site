package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static com.econnect.barangaymanagementapp.enumeration.modal.Modal.ERROR;

public class FormValidator {
    private final ModalUtils modalUtils;
    private final Map<TextField, ChangeListener<Boolean>> textFieldListenerMap = new HashMap<>();
    private final Map<DatePicker, ChangeListener<Boolean>> datePickerListenerMap = new HashMap<>();
    private final Map<ComboBox, ChangeListener<Boolean>> comboBoxListenerMap = new HashMap<>();

    public final Predicate<String> IS_NOT_EMPTY = text -> text != null && !text.trim().isEmpty();
    public final Predicate<String> IS_NUMBER = text -> text.matches("\\d+");
    public final Predicate<String> IS_EMAIL = text -> text.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    // To be fixed with + 639
    public final Predicate<String> IS_VALID_PHONE = text -> text.matches("^\\+?[0-9]{10,15}$");
    public final Predicate<String> DATE_VALIDATOR = text -> text.matches("\\d{1,2}/\\d{1,2}/\\d{4}");

    public FormValidator(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    public void validateFile(File file, HBox buttonContainer) {
        hasEmptyFile(file, buttonContainer);
    }

    public void setupDatePicker(LocalDate minDate, LocalDate maxDate, DatePicker... datePickers) {
        Arrays.stream(datePickers).forEach(node -> {
            node.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(date.isBefore(minDate) || date.isAfter(maxDate));
                }
            });
            ChangeListener<Boolean> listener = (obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    handleDateInput(node, minDate, maxDate);
                }
            };
            node.getEditor().focusedProperty().addListener(listener);
            datePickerListenerMap.put(node, listener);

            node.getEditor().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    handleDateInput(node, minDate, maxDate);
                }
            });
        });
    }

    private void handleDateInput(DatePicker datePicker, LocalDate minDate, LocalDate maxDate) {
        LocalDate date = null;
        try {
            String text = datePicker.getEditor().getText();
            if (!DATE_VALIDATOR.test(text)) {
                modalUtils.showModal(ERROR, "Invalid Date", "Please enter a valid date in the format MM/DD/YYYY.");
                throw new Exception();
            }
            date = datePicker.getConverter().fromString(text);
            if (date == null || date.isBefore(minDate) || date.isAfter(maxDate)) {
                modalUtils.showModal(ERROR, "Invalid Date", "Please enter a valid date within the range.");
                throw new Exception();
            }
            datePicker.setValue(date);
            datePicker.setStyle(null);
        } catch (Exception e) {
            datePicker.getEditor().setText("");
            datePicker.setValue(null);
            datePicker.setStyle("-fx-border-color: red;");
        }
    }

    public boolean hasEmptyFile(File file, HBox buttonContainer) {
        if (file == null) {
            buttonContainer.setStyle("-fx-border-color: red;");
            return true;
        } else {
            buttonContainer.setStyle(null);
            return false;
        }
    }

    public boolean hasEmptyComboBox(ComboBox<?> comboBox) {
        if (comboBox.getValue() == null) {
            comboBox.setStyle("-fx-border-color: red;");
            return true;
        } else {
            comboBox.setStyle(null);
            return false;
        }
    }

    public void addListeners(Node node, Predicate<String> validator, String errorMessage) {
        switch (node) {
            case TextField textField -> {
                ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
                    if (!newValue) {
                        if (!validator.test(textField.getText())) {
                            textField.setStyle("-fx-border-color: red;");
                            modalUtils.showModal(ERROR, "Error", errorMessage);
                        } else {
                            textField.setStyle(null);
                        }
                    }
                };
                textField.focusedProperty().addListener(listener);
                textFieldListenerMap.put(textField, listener);
            }
            case ComboBox<?> comboBox -> {
                ChangeListener<Boolean> comboBoxListener = (_, _, newValue) -> {
                    if (!newValue) {
                        if (comboBox.getValue() == null) {
                            comboBox.setStyle("-fx-border-color: red;");
                            modalUtils.showModal(ERROR, "Error", "Please select a valid option.");
                        } else {
                            comboBox.setStyle(null);
                        }
                    }
                };

                comboBox.focusedProperty().addListener(comboBoxListener);
                comboBoxListenerMap.put(comboBox, comboBoxListener);
            }
            default -> throw new IllegalArgumentException("Unsupported node type: " + node.getClass().getSimpleName());
        }
    }

    public void removeListeners() {
        textFieldListenerMap.forEach((textField, listener) -> textField.focusedProperty().removeListener(listener));
        textFieldListenerMap.clear();

        datePickerListenerMap.forEach((datePicker, listener) -> datePicker.getEditor().focusedProperty().removeListener(listener));
        datePickerListenerMap.clear();

        comboBoxListenerMap.forEach((comboBox, listener) -> comboBox.focusedProperty().removeListener(listener));
        comboBoxListenerMap.clear();
    }
}

