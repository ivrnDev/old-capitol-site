package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
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
import java.util.function.Predicate;

import static com.econnect.barangaymanagementapp.enumeration.modal.Modal.ERROR;

public class FormValidator {
    private final ModalUtils modalUtils;
    public final Predicate<String> IS_NOT_EMPTY = text -> text != null && !text.trim().isEmpty();
    public final Predicate<String> IS_NUMBER = text -> text.matches("\\d+");
    public final Predicate<String> IS_EMAIL = text -> text.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    public final Predicate<String> IS_VALID_PHONE = text -> text.matches("^\\+?[0-9]{10,15}$");

    public FormValidator(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    public boolean hasEmptyField(Node... nodes) {
        final boolean[] hasEmptyField = {false};

        Arrays.stream(nodes).forEach(node -> {
            switch (node) {
                case TextField textField -> {
                    if (textField.getText() == null || textField.getText().trim().isEmpty()) {
                        textField.setStyle("-fx-border-color: red;");
                        addListeners(textField);
                        hasEmptyField[0] = true;
                    } else {
                        textField.setStyle(null);
                    }
                }
                case ComboBox<?> comboBox -> {
                    if (comboBox.getValue() == null) {
                        comboBox.setStyle("-fx-border-color: red;");
                        addListeners(comboBox);
                        hasEmptyField[0] = true;
                    } else {
                        comboBox.setStyle(null);
                    }
                }
//                case DatePicker datePicker -> {
//                    if (datePicker.getValue() == null) {
//                        datePicker.setStyle("-fx-border-color: red;");
//                        hasEmptyField[0] = true;
//                    }
//                }
                default -> {
                    System.out.println("Unsupported node type: " + node.getClass().getSimpleName());
                }
            }
        });

        return hasEmptyField[0];
    }

    public void validateFile(File file, HBox buttonContainer) {
        hasEmptyFile(file, buttonContainer);
    }

    public void setupDatePicker(LocalDate minDate, LocalDate maxDate, DatePicker... datePickers) {
        Arrays.stream(datePickers).forEach(node -> {
            node.valueProperty().addListener(_ -> validateDate(minDate, maxDate, node));
            node.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date.isAfter(maxDate) || date.isBefore(minDate)) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ece0e1;");
                    }
                }
            });
        });
    }

    public boolean validateDate(LocalDate minDate, LocalDate maxDate, DatePicker... datePickers) {
        System.out.println("CALL VALIDATE DATE");
        boolean hasError = false;
        boolean hasNoValue = false;

        if (datePickers.length == 0) {
            return false;
        }

        for (DatePicker datePicker : datePickers) {
            datePicker.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    handleDateInput(datePicker);
                }
            });

//            datePicker.getEditor().setOnKeyPressed(event -> {
//                if (event.getCode() == KeyCode.ENTER) {
//                    handleDateInput(datePicker);
//                }
//            });

            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate == null) {
                datePicker.setStyle("-fx-border-color: red;");
                hasNoValue = true;
            } else if (selectedDate.isBefore(minDate) || selectedDate.isAfter(maxDate)) {
                datePicker.setStyle("-fx-border-color: red;");
                hasError = true;
            } else {
                datePicker.setStyle(null);
            }
        }

        if (hasError) {
            modalUtils.showModal(ERROR, "Invalid Date", "Selected date is not within the allowed range.");
        }

        if (hasNoValue) {
            modalUtils.showModal(ERROR, "Invalid Date", "Please enter a valid date format.");
        }

        return hasError || hasNoValue;
    }

    private void handleDateInput(DatePicker datePicker) {
        LocalDate date = null;
        try {
            date = datePicker.getConverter().fromString(datePicker.getEditor().getText());
            datePicker.setValue(date);
            datePicker.setStyle(null);
        } catch (Exception e) {
            datePicker.getEditor().setText("");
            datePicker.setValue(null);
            datePicker.setStyle("-fx-border-color: red;");
            modalUtils.showModal(ERROR, "Invalid Date", "Please enter a valid date format.");
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

    private void addListeners(Node node) {
        switch (node) {
            case TextField textField -> {
                textField.focusedProperty().addListener((_, _, newValue) -> {
                    if (newValue) {
                        textField.setStyle(null);
                    }
                });
                textField.setOnKeyTyped(_ -> textField.setStyle(null));
            }
            case ComboBox<?> comboBox -> {
                comboBox.valueProperty().addListener((_, _, newValue) -> {
                    if (newValue != null) {
                        comboBox.setStyle(null);
                    }
                });
            }
            default -> throw new IllegalArgumentException("Unsupported node type: " + node.getClass().getSimpleName());
        }
    }
}

