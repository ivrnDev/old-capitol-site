package com.econnect.barangaymanagementapp.enumeration.modal;

import com.econnect.barangaymanagementapp.enumeration.type.SoundType;

import static com.econnect.barangaymanagementapp.enumeration.modal.ModalType.*;

public enum Modal {
    CLASSIC("view/modal/classic.fxml", SoundType.DEFAULT, MODAL, "default-style", null),
    DEFAULT("view/modal/default.fxml", SoundType.DEFAULT, MODAL, "default-style", "default-button"),
    DEFAULT_APPROVE("view/modal/default.fxml", SoundType.DEFAULT, MODAL, "default-style", "approve-button"),
    DEFAULT_REJECT("view/modal/default.fxml", SoundType.DEFAULT, MODAL, "default-style", "reject-button"),
    SUCCESS("view/modal/notification.fxml", SoundType.SUCCESS, NOTIFICATION, "success-style", null, "#026917", "Icon/success.png"),
    WARNING("view/modal/notification.fxml", SoundType.WARNING, NOTIFICATION, "warning-style", null, "#9e9600", "Icon/warning.png"),
    ERROR("view/modal/notification.fxml", SoundType.ERROR, NOTIFICATION, "error-style", null, "#b30707", "Icon/error.png");

    private final String fxmlPath;
    private final SoundType sound;
    private final ModalType modalType;
    private final String rootStyle;
    private final String buttonStyle;
    private final String textColor;
    private final String iconPath;

    // Main constructor with all parameters
    Modal(String fxmlPath, SoundType sound, ModalType modalType, String rootStyle, String buttonStyle, String textColor, String iconPath) {
        this.fxmlPath = fxmlPath;
        this.sound = sound;
        this.modalType = modalType;
        this.rootStyle = rootStyle;
        this.buttonStyle = buttonStyle;
        this.textColor = textColor;
        this.iconPath = iconPath;

    }

    // Overloaded constructor for modals without text color customization (defaults to black)
    Modal(String fxmlPath, SoundType sound, ModalType modalType, String rootStyle, String buttonStyle) {
        this(fxmlPath, sound, modalType, rootStyle, buttonStyle, "#000000", null);
    }

    public String getFxmlPath() {
        return fxmlPath.toLowerCase();
    }

    public SoundType getSound() {
        return sound;
    }

    public ModalType getModalType() {
        return modalType;
    }

    public String getRootStyle() {
        return rootStyle;
    }

    public String getButtonStyle() {
        return buttonStyle;
    }

    public String getTextColor() {
        return textColor;
    }

    public String getIcon() {
        return iconPath;
    }
}

