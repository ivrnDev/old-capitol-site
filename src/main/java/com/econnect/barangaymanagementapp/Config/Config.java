package com.econnect.barangaymanagementapp.Config;

public class Config {
    public static String getFirebaseUrl() {
        return System.getenv("FIREBASE_URL");
    }

}
