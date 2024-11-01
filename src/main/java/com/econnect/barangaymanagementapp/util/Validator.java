package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.converter.DefaultStringConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.function.Predicate;

import static com.econnect.barangaymanagementapp.enumeration.modal.Modal.ERROR;

public class Validator {
    private final ModalUtils modalUtils;
    private String errorTitle;
    private String errorMessage;
    private boolean hasError;

    public Validator(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    public boolean validate(TextField inputField, VALIDATOR_TYPE validatorType) {
        return validatorType.getPredicate().test(inputField.getText());
    }

    public boolean checkBox(CheckBox[] checkBoxes) {
        boolean hasSelected = false;
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                hasSelected = true;
            }
        }

        if (hasSelected) {
            return false;
        }

        if (hasError) {
            errorTitle = "Failed";
            errorMessage = "Please select at least one certificate to be issued";
            triggerError();
            return true;
        }
        return true;
    }

    public boolean hasEmptyFields(TextField[] textFields) {
        for (TextField textField : textFields) {
            if (textField.getText().isEmpty()) {
                if (!hasError) {
                    hasError = true;
                    errorTitle = "Failed";
                    errorMessage = "Please fill out all required fields";
                }
                textField.setStyle("-fx-border-color: red");
            } else {
                hasError = false;
                textField.setStyle("");
            }
            addTextFieldListener(textField);
        }

        if (hasError) {
            triggerError();
            return true;
        }
        return false;
    }

    public boolean hasEmptyFields(TextField[] textFields, DatePicker[] datePickers) {
        for (TextField textField : textFields) {
            if (textField.getText().isEmpty()) {
                if (!hasError) {
                    hasError = true;
                    errorTitle = "Failed";
                    errorMessage = "Please fill out all required fields";
                }
                textField.setStyle("-fx-border-color: red");
            } else {
                hasError = false;
                textField.setStyle("");
            }
            addTextFieldListener(textField);
        }

        for (DatePicker datePicker : datePickers) {
            if (datePicker.getEditor().getText().isEmpty()) {
                if (!hasError) {
                    hasError = true;
                    errorTitle = "Failed";
                    errorMessage = "Please fill out all required fields";
                }
                datePicker.setStyle("-fx-border-color: red");
            } else {
                hasError = false;
                datePicker.setStyle("");
            }
        }

        if (hasError) {
            triggerError();
            return true;
        }
        return false;
    }

    public void addTextFieldListener(TextField textField) {
        textField.setOnKeyTyped(_ -> {
            if (!textField.getText().isEmpty()) {
                textField.setStyle("");
            }
        });
    }

    public void setupDatePicker(LocalDate minDate, LocalDate maxDate, DatePicker... datePickers) {
        Arrays.stream(datePickers).forEach(datePicker -> {
            datePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(date.isBefore(minDate) || date.isAfter(maxDate));
                }
            });
            datePicker.getEditor().setTextFormatter(new TextFormatter<>(change -> {
                String newText = change.getControlNewText();

                if (newText.length() > 10 || !newText.matches("[\\d/]*")) {
                    return null;
                }

                if (newText.length() >= 2) {
                    String month = newText.substring(0, 2);
                    if (Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12) {
                        return null;
                    }
                }

                if (newText.length() >= 5) {
                    String day = newText.substring(3, 5);
                    if (Integer.parseInt(day) < 1 || Integer.parseInt(day) > 31) {
                        return null;
                    }
                }

                if (newText.length() == 10) {
                    try {
                        LocalDate date = LocalDate.parse(newText, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                        if (date.isBefore(minDate) || date.isAfter(maxDate)) {
                            return null;
                        }
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                }

                int length = newText.length();
                if (length == 2 || length == 5) {
                    if (!newText.endsWith("/")) {
                        newText = newText.substring(0, length) + "/" + newText.substring(length);
                        change.setText(newText);
                        change.setRange(0, change.getControlText().length());
                    }
                }

                if (length < change.getControlText().length()) {
                    if (length == 2 || length == 5) {
                        newText = newText.substring(0, length - 1);
                        change.setText(newText);
                        change.setRange(0, change.getControlText().length());
                    }
                }

                String finalNewText = newText;
                Platform.runLater(() -> datePicker.getEditor().positionCaret(finalNewText.length()));

                return change;
            }));

            datePicker.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    String text = datePicker.getEditor().getText();
                    if (text.length() == 10) {
                        try {
                            LocalDate date = LocalDate.parse(text, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                            if (date.isBefore(minDate) || date.isAfter(maxDate)) {
                                throw new DateTimeParseException("Date out of range", text, 0);
                            }
                            datePicker.setValue(date);
                        } catch (DateTimeParseException e) {
                            datePicker.getEditor().setText("");
                            datePicker.setValue(null);
                            datePicker.setStyle("-fx-border-color: red;");
                        }
                    } else {
                        datePicker.getEditor().setText("");
                        datePicker.setValue(null);
                        datePicker.setStyle("-fx-border-color: red;");
                    }
                } else {
                    datePicker.setStyle(null);
                }
            });

        });
    }

    private void triggerError() {
        modalUtils.showModal(Modal.ERROR, errorTitle, errorMessage);
    }

    // Methods that are separated from the original class functionality
    public void createResidentIdFormatter(TextField inputField) {
        inputField.setTextFormatter(new TextFormatter<>(new DefaultStringConverter(), "", change -> {
            if (change.isContentChange()) {
                String newText = change.getControlNewText().replaceAll("[^\\d]", "");
                int length = newText.length();

                if (length > 11) {
                    return null;
                }

                if (length > 7) {
                    newText = newText.substring(0, 7) + "-" + newText.substring(7);
                }

                change.setText(newText);
                change.setRange(0, change.getControlText().length());
                String finalNewText = newText;
                Platform.runLater(() -> inputField.positionCaret(finalNewText.length()));
            }
            return change;
        }));
    }

    public void createUnitNumberFormatter(TextField inputField, int minLength, int maxLength) {
        inputField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText().replaceAll("\\s+(KG|FT)$", "");

            if (newText.isBlank()) {
                return change;
            }

            if (!newText.matches("\\d*")) {
                return null;
            }

            try {
                int value = Integer.parseInt(newText);
                if (value >= minLength && value <= maxLength) {
                    return change;
                }
            } catch (NumberFormatException e) {
                return null;
            }

            return null;
        }));
    }

    public void setUnitFocusedProperty(TextField inputField, String unit) {
        inputField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = inputField.getText();
                if (!text.isEmpty() && !text.endsWith(unit)) {
                    inputField.setText(text + " " + unit);
                }
            }
        });
    }

    @AllArgsConstructor
    @Getter
    public enum VALIDATOR_TYPE {
        IS_EMPTY(input -> input.isEmpty()),
        IS_NUMBER(input -> input.matches("\\d+")),
        IS_EMAIL(input -> input.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")),
        IS_VALID_PHONE(input -> input.matches("\\d{10}")),
        IS_VALID_TELEPHONE(input -> input.matches("\\d{9}")),
        DATE_VALIDATOR(input -> input.matches("\\d{1,2}/\\d{1,2}/\\d{4}"));

        private final Predicate<String> predicate;
    }

}



