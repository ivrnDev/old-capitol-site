package com.econnect.barangaymanagementapp.enumeration.database;

public enum Firestore {
    PROFILE_PICTURE("/1x1Images", "1x1Images"),
    VALID_ID("/ValidIDs", "ValidIDs"),
    ANNOUNCEMENT("/announcement_images", "announcement_images"),
    RESUME("/3-Resume", "Resume"),
    SAMPLE("/sample", "sample");
    public final String path;
    public final String name;

    Firestore(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}
