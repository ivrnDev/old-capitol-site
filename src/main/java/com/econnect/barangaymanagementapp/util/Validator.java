package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Validator {
    private final ModalUtils modalUtils;
    private String errorTitle;
    private String errorMessage;

    public Validator(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    //Setup Fields
    public boolean hasEmptyCheckBox(CheckBox[] checkBoxes, HBox container) {
        boolean hasError = true;
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) hasError = false;
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    container.setStyle(null);
                } else {
                    boolean anySelected = Arrays.stream(checkBoxes).anyMatch(CheckBox::isSelected);
                    if (!anySelected) {
                        container.setStyle("-fx-border-color: red;");
                    }
                }
            });
        }

        if (hasError) {
            container.setStyle("-fx-border-color: red;");
            return true;
        }
        container.setStyle(null);
        return false;
    }

    public boolean hasEmptyFields(TextField... textFields) {
        boolean hasError = false;
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

    public boolean hasEmptyFields(TextField[] textFields, DatePicker[] datePickers, ComboBox<String>[] comboBox) {
        boolean hasError = false;
        for (TextField textField : textFields) {
            if (textField.getText().isEmpty()) {
                if (!hasError) {
                    hasError = true;
                    errorTitle = "Failed";
                    errorMessage = "Please fill out all required fields";
                }
                textField.setStyle("-fx-border-color: red");
            } else if (textField.getText().length() < 3) {
                if (!hasError) {
                    hasError = true;
                    errorTitle = "Invalid";
                    errorMessage = "Input must be at least 2 characters";
                }
            } else {
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
                datePicker.setStyle("");
            }
        }

        for (ComboBox<String> comboBox1 : comboBox) {
            if (comboBox1.getValue() == null) {
                if (!hasError) {
                    hasError = true;
                    errorTitle = "Failed";
                    errorMessage = "Please fill out all required fields";
                }
                comboBox1.setStyle("-fx-border-color: red");
            } else {
                comboBox1.setStyle("");
            }
        }

        if (hasError) {
            triggerError();
            return true;
        }
        return false;
    }

    public void setupDatePicker(LocalDate minDate, LocalDate maxDate, DatePicker... datePickers) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        Arrays.stream(datePickers).forEach(datePicker -> {
            datePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(date.isBefore(minDate) || date.isAfter(maxDate));
                }
            });

            datePicker.setConverter(new StringConverter<LocalDate>() {
                @Override
                public String toString(LocalDate date) {
                    return date != null ? formatter.format(date) : "";
                }

                @Override
                public LocalDate fromString(String string) {
                    try {
                        return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                    } catch (DateTimeParseException e) {
                        return null;
                    }
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
                        LocalDate date = LocalDate.parse(newText, formatter);
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
                            LocalDate date = LocalDate.parse(text, formatter);
                            if (date.isBefore(minDate) || date.isAfter(maxDate)) {
                                throw new DateTimeParseException("Date out of range", text, 0);
                            }
                            datePicker.setValue(date);
                        } catch (DateTimeParseException e) {
                            Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Invalid", "Please enter a valid date in the format MM/DD/YYYY."));
                            datePicker.getEditor().setText("");
                            datePicker.setValue(null);
                            datePicker.setStyle("-fx-border-color: red;");
                        }
                    } else {
                        Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Invalid", "Please enter a valid date in the format MM/DD/YYYY."));
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

    public void setupComboBox(List<ComboBox<String>> comboBox) {
        for (ComboBox<String> box : comboBox) {
            box.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    box.setStyle(null);
                } else if (box.getValue() == null) {
                    box.setStyle("-fx-border-color: red;");
                }
            });
        }
    }

    public boolean hasEmptyFiles(File[] files, HBox[] fileContainers) {
        boolean hasError = false;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            HBox buttonContainer = fileContainers[i];
            if (file == null) {
                buttonContainer.setStyle("-fx-border-color: red;");
                if (!hasError) {
                    hasError = true;
                    errorTitle = "Failed";
                    errorMessage = "Please upload all required files";
                }
            } else {
                buttonContainer.setStyle(null);
            }
        }

        if (hasError) {
            triggerError();
            return true;
        }
        return false;
    }

    public void hasEmptyFile(File file, HBox buttonContainer) {
        if (file == null) {
            buttonContainer.setStyle("-fx-border-color: red;");
        } else {
            buttonContainer.setStyle(null);
        }
    }

    public void setupResidentIdInput(TextField inputField) {
        createResidentIdFormatter(inputField);
        inputField.setOnKeyTyped(_ -> {
            if (!inputField.getText().isEmpty()) {
                inputField.setStyle("");
            }
        });
    }

    // Event Listeners
    private void addTextFieldFormatterListener(TextField textField, VALIDATOR_TYPE validatorType, String errorMessage) {
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!validatorType.getPredicate().test(textField.getText())) {
                    textField.setStyle("-fx-border-color: red;");
                    textField.setText("");
                    Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Invalid", errorMessage));
                } else {
                    textField.setStyle(null);
                }
            }
        });
    }

    public void addTextFieldListener(TextField textField) {
        textField.setOnKeyTyped(_ -> {
            if (!textField.getText().isEmpty()) {
                textField.setStyle("");
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (textField.getText().length() < 3) {
                    textField.setStyle("-fx-border-color: red;");
                    Platform.runLater(() -> modalUtils.showModal(Modal.ERROR, "Invalid", "Input must be at least 2 characters"));
                }
            }
        });
    }

    public void setLettersOnlyListener(int maxLength, TextField... textField) {
        for (TextField field : textField) {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("[a-zA-Z ]*")) {
                    field.setText(newValue.replaceAll("[^a-zA-Z ]", ""));
                }
                if (field.getText().length() > maxLength) {
                    field.setText(oldValue);
                }
            });
        }
    }

    // Formatters
    public void createMobileNumberFormatter(int maxChar, TextField... textFields) {
        for (TextField textField : textFields) {
            addTextFieldFormatterListener(textField, VALIDATOR_TYPE.IS_VALID_PHONE, "Please enter a valid mobile number");
            textField.setTextFormatter(new TextFormatter<>(change -> {
                String newText = change.getControlNewText().replaceAll("[^\\d]", "");
                if (newText.length() > maxChar) {
                    return null;
                }

                change.setText(newText);
                change.setRange(0, change.getControlText().length());
                return change;
            }));
        }
    }

    public void createTelephoneFormatter(TextField inputField) {
        inputField.setTextFormatter(new TextFormatter<>(new DefaultStringConverter(), "", change -> {
            if (change.isContentChange()) {
                String newText = change.getControlNewText().replaceAll("[^\\d]", "");
                int length = newText.length();

                if (length > 8) {
                    return null;
                }

                if (length > 4) {
                    newText = newText.substring(0, 4) + "-" + newText.substring(4);
                }

                change.setText(newText);
                change.setRange(0, change.getControlText().length());
                String finalNewText = newText;
                Platform.runLater(() -> inputField.positionCaret(finalNewText.length()));
            }
            return change;
        }));
        addTextFieldFormatterListener(inputField, VALIDATOR_TYPE.IS_VALID_TELEPHONE, "Please enter a valid telephone number");
    }

    public void createEmailFormatter(TextField inputField) {
        addTextFieldFormatterListener(inputField, VALIDATOR_TYPE.IS_EMAIL, "Please enter a valid email address");
    }

    public void createTinIdFormatter(TextField inputField) {
        inputField.setTextFormatter(new TextFormatter<>(new DefaultStringConverter(), "", change -> {
            if (change.isContentChange()) {
                String newText = change.getControlNewText().replaceAll("[^\\d]", "");
                int length = newText.length();

                if (length > 14) {
                    return null;
                }

                if (length > 9) {
                    newText = newText.substring(0, 3) + "-" + newText.substring(3, 6) + "-" + newText.substring(6, 9) + "-" + newText.substring(9);
                } else if (length > 6) {
                    newText = newText.substring(0, 3) + "-" + newText.substring(3, 6) + "-" + newText.substring(6);
                } else if (length > 3) {
                    newText = newText.substring(0, 3) + "-" + newText.substring(3);
                }

                change.setText(newText);
                change.setRange(0, change.getControlText().length());
                String finalNewText = newText;
                Platform.runLater(() -> inputField.positionCaret(finalNewText.length()));
            }
            return change;
        }));
        addTextFieldFormatterListener(inputField, VALIDATOR_TYPE.IS_VALID_TIN_ID, "Please enter a valid TIN ID");
    }

    private void createResidentIdFormatter(TextField inputField) {
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

    public void createWeightFormatter(TextField inputField) {
        setUnitFocusedProperty(inputField, "KG");
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
                if (value >= 1 && value <= 200) {
                    return change;
                }
            } catch (NumberFormatException e) {
                return null;
            }

            return null;
        }));
    }

    public void createHeightFormatter(TextField inputField) {
        setUnitFocusedProperty(inputField, "FT");
        inputField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText().replaceAll("\\s+(FT)$", ""); // Remove "FT" while typing

            if (!newText.matches("\\d*'?(\\d*)")) {
                return null;
            }

            if (newText.length() == 2 && !newText.contains("'")) {
                int feet = Character.getNumericValue(newText.charAt(0));
                int inches = Character.getNumericValue(newText.charAt(1));
                if (feet >= 1 && inches >= 0 && inches <= 9) {
                    change.setText(feet + "'" + inches);
                    change.setRange(0, change.getControlText().length());
                    Platform.runLater(() -> inputField.positionCaret(change.getControlNewText().length()));
                    return change;
                }
            }

            if (newText.length() > 4) {
                return null;
            }

            return change;
        }));
    }

    public void setUnitFocusedProperty(TextField inputField, String unit) {
        inputField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = inputField.getText();
                if (!text.isEmpty() && !text.endsWith(unit)) {
                    inputField.setText(text + " " + unit);
                }
            } else {
                String text = inputField.getText();
                if (text.endsWith(" " + unit)) {
                    inputField.setText(text.replace(" " + unit, "")); // remove unit on focus
                }
            }
        });
    }

    //Validator
    public boolean validate(TextField inputField, VALIDATOR_TYPE validatorType) {
        return validatorType.getPredicate().test(inputField.getText());
    }

    private void triggerError() {
        modalUtils.showModal(Modal.ERROR, errorTitle, errorMessage);
    }

    @AllArgsConstructor
    @Getter
    public enum VALIDATOR_TYPE {
        IS_EMPTY(input -> input.isEmpty()),
        IS_NUMBER(input -> input.matches("\\d+")),
        IS_EMAIL(input -> input.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")),
        IS_VALID_PHONE(input -> input.matches("09\\d{9}")),
        IS_VALID_TELEPHONE(input -> input.matches("\\d{4}-\\d{4}")),
        DATE_VALIDATOR(input -> input.matches("\\d{1,2}/\\d{1,2}/\\d{4}")),
        IS_VALID_TIN_ID(input -> input.matches("\\d{3}-\\d{3}-\\d{3}-\\d{5}"));
        private final Predicate<String> predicate;
    }

}


