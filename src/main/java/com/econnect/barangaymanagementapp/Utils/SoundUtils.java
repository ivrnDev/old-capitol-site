package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Enumeration.Sound;
import com.econnect.barangaymanagementapp.MainApplication;

import javax.sound.sampled.*;
import java.io.IOException;

public class SoundUtils {
    public void playSound(Sound soundType) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(MainApplication.class.getResource(soundType.getSoundPath()));
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }

}
