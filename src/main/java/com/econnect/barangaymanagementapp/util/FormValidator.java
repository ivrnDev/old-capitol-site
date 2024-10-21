package com.econnect.barangaymanagementapp.util;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.util.Arrays;

public class FormValidator {
    public boolean hasEmptyField(Node... nodes) {
        final boolean[] hasEmptyField = {false}; // Track if any field is empty

        // Process each node
        Arrays.stream(nodes).forEach(node -> {
            switch (node) {
                case TextField textField -> {
                    if (textField.getText() == null || textField.getText().trim().isEmpty()) {
                        textField.setStyle("-fx-border-color: red;");
                        addListeners(textField);
                        hasEmptyField[0] = true; // Mark as invalid
                    } else {
                        textField.setStyle(null); // Reset style if valid
                    }
                }
                case ComboBox<?> comboBox -> {
                    if (comboBox.getValue() == null) {
                        comboBox.setStyle("-fx-border-color: red;");
                        addListeners(comboBox);
                        hasEmptyField[0] = true; // Mark as invalid
                    } else {
                        comboBox.setStyle(null); // Reset style if valid
                    }
                }
                case DatePicker datePicker -> {
                    if (datePicker.getValue() == null) {
                        datePicker.setStyle("-fx-border-color: red;");
                        addListeners(datePicker);
                        hasEmptyField[0] = true; // Mark as invalid
                    } else {
                        datePicker.setStyle(null); // Reset style if valid
                    }
                }
                default -> {
                    System.out.println("Unsupported node type: " + node.getClass().getSimpleName());
                }
            }
        });

        return hasEmptyField[0];
    }

    public void addListeners(Node node) {
        switch (node) {
            case TextField textField -> {
                textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        textField.setStyle(null);
                    }
                });
                textField.setOnKeyTyped(_ -> textField.setStyle(null));
            }
            case ComboBox<?> comboBox -> {
                comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        comboBox.setStyle(null);
                    }
                });
            }
            case DatePicker datePicker -> {
                datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        datePicker.setStyle(null);
                    }
                });
            }
            default -> throw new IllegalArgumentException("Unsupported node type: " + node.getClass().getSimpleName());
        }
    }
}

