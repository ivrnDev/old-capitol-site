package com.econnect.barangaymanagementapp.enumeration.ui;

public enum ButtonStyle {
    ACCEPT("h-accept"),
    REJECT("h-reject"),
    VIEW("h-view"),
    UPDATE("h-update"),
    WARNING("h-warning");

    private final String rootStyle;

    ButtonStyle(String rootStyle) {
        this.rootStyle = rootStyle;
    }

    public String getRootStyle() {
        return rootStyle;
    }
}
