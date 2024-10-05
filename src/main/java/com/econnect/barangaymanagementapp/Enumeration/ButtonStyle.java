package com.econnect.barangaymanagementapp.Enumeration;

public enum ButtonStyle {
    ACCEPT("h-accept"),
    REJECT("h-reject"),
    VIEW("h-view"),
    UPDATE("h-update");

    private final String rootStyle;

    ButtonStyle(String rootStyle) {
        this.rootStyle = rootStyle;
    }

    public String getRootStyle() {
        return rootStyle;
    }
}
