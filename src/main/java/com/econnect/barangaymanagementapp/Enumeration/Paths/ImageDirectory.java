package com.econnect.barangaymanagementapp.Enumeration.Paths;

public enum ImageDirectory {
    PROFILE_PICTURE("/1x1Images", "1x1Images"),
    VALID_ID("/ValidIDs", "ValidIDs"),
    ANNOUNCEMENT("/announcement_images", "announcement_images"),
    SAMPLE("/sample", "sample");
    public final String path;
    public final String name;

    ImageDirectory(String path, String name) {
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
