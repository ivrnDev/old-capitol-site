package com.econnect.barangaymanagementapp.util;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.NumberStringConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Predicate;

@AllArgsConstructor
public class Validator {
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

    public boolean validate(TextField inputField, VALIDATOR_TYPE validatorType) {
        return validatorType.getPredicate().test(inputField.getText());
    }
}


