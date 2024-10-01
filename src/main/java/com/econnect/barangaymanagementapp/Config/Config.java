package com.econnect.barangaymanagementapp.Config;

public class Config {
    public static String getFirebaseUrl() {
        String firebaseUrl = System.getenv("FIREBASE_URL");

        if (firebaseUrl == null || firebaseUrl.isEmpty()) {
            throw new IllegalArgumentException("FIREBASE_URL is not set");
        }

        return firebaseUrl;
    }
}
