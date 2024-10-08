package com.econnect.barangaymanagementapp.util.resource;

import com.econnect.barangaymanagementapp.enumeration.type.SoundType;
import com.econnect.barangaymanagementapp.MainApplication;

import javax.sound.sampled.*;
import java.io.IOException;

public class SoundUtils {
    public void playSound(SoundType soundType) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(MainApplication.class.getResource(soundType.getSoundPath()));
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }

}
