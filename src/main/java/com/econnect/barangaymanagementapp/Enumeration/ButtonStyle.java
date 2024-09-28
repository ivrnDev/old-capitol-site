package com.econnect.barangaymanagementapp.Enumeration;

public enum ButtonStyle {
    ACCEPT("accept"),
    REJECT("reject"),
    ADD("add");
    private final String rootStyle;

    ButtonStyle(String rootStyle) {
        this.rootStyle = rootStyle;
    }

    public String getRootStyle() {
        return rootStyle;
    }
}
