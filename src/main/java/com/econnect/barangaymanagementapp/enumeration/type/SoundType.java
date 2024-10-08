package com.econnect.barangaymanagementapp.enumeration.type;


public enum SoundType {
    DEFAULT("audio/default.wav"),
    ERROR("audio/error.wav"),
    SUCCESS("audio/success.wav"),
    WARNING("audio/warning.wav");


    private final String soundPath;

    SoundType(String soundPath) {
        this.soundPath = soundPath;
    }

    public String getSoundPath() {
        return soundPath;
    }

}
