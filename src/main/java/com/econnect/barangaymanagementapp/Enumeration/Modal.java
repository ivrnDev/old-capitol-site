package com.econnect.barangaymanagementapp.Enumeration;

import static com.econnect.barangaymanagementapp.Enumeration.ModalType.*;

public enum Modal {
    CLASSIC("View/Component/Modal/classic.fxml", Sound.DEFAULT, MODAL, "default-style", null),
    DEFAULT("View/Component/Modal/default.fxml", Sound.DEFAULT, MODAL, "default-style", "default-button"),
    DEFAULT_APPROVE("View/Component/Modal/default.fxml", Sound.DEFAULT, MODAL, "default-style", "approve-button"),
    DEFAULT_REJECT("View/Component/Modal/default.fxml", Sound.DEFAULT, MODAL, "default-style", "reject-button"),
    SUCCESS("View/Component/Modal/notification.fxml", Sound.SUCCESS, NOTIFICATION, "success-style", null, "#026917"),
    WARNING("View/Component/Modal/notification.fxml", Sound.WARNING, NOTIFICATION, "warning-style", null, "#9e9600"),
    ERROR("View/Component/Modal/notification.fxml", Sound.ERROR, NOTIFICATION, "error-style", null, "#b30707");

    private final String fxmlPath;
    private final Sound sound;
    private final ModalType modalType;
    private final String rootStyle;
    private final String buttonStyle;
    private final String textColor;

    // Main constructor with all parameters
    Modal(String fxmlPath, Sound sound, ModalType modalType, String rootStyle, String buttonStyle, String textColor) {
        this.fxmlPath = fxmlPath;
        this.sound = sound;
        this.modalType = modalType;
        this.rootStyle = rootStyle;
        this.buttonStyle = buttonStyle;
        this.textColor = textColor;
    }

    // Overloaded constructor for modals without text color customization (defaults to black)
    Modal(String fxmlPath, Sound sound, ModalType modalType, String rootStyle, String buttonStyle) {
        this(fxmlPath, sound, modalType, rootStyle, buttonStyle, "#000000");  // Default text color is black
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public Sound getSound() {
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
}

