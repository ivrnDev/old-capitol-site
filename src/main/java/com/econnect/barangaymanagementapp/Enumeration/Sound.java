package com.econnect.barangaymanagementapp.Enumeration;


public enum Sound {
    DEFAULT("Audio/default.wav"),
    ERROR("Audio/error.wav"),
    SUCCESS("Audio/success.wav"),
    WARNING("Audio/warning.wav");


    private final String soundPath;

    Sound(String soundPath) {
        this.soundPath = soundPath;
    }

    public String getSoundPath() {
        return soundPath;
    }

}
