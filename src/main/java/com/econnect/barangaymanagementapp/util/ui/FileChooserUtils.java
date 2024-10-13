package com.econnect.barangaymanagementapp.util.ui;

import javafx.stage.FileChooser;

import java.io.File;

public class FileChooserUtils {
    public FileChooser createFileChooser() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        String userHome = System.getProperty("user.home");
        File picturesDirectory = new File(userHome, "Pictures");
        if (picturesDirectory.exists()) {
            fileChooser.setInitialDirectory(picturesDirectory);
        }
        return fileChooser;
    }
}
