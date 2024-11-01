package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DefaultStringConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Predicate;

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

    public boolean textFields(TextField[] textFields) {
        for (TextField textField : textFields) {
            if (textField.getText().isEmpty()) {
                if (!hasError) {
                    hasError = true;
                    errorTitle = "Failed";
                    errorMessage = "Please fill out all the fields";
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

    private void triggerError() {
        modalUtils.showModal(Modal.ERROR, errorTitle, errorMessage);
    }

    private void addTextFieldListener(TextField textField) {
        textField.setOnKeyTyped(_ -> {
            if (!textField.getText().isEmpty()) {
                textField.setStyle("");
            }
        });
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


