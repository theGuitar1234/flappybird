package org.game.util;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;

public class AudioLoader {
    public static Clip loadClip(String resourcePath) {
        try {
            URL url = AudioLoader.class.getResource(resourcePath);
            if (url == null) throw new RuntimeException("Missing audio resource: " + resourcePath);

            // AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            // Clip clip = AudioSystem.getClip();
            // clip.open(audioInputStream);

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);

            DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());

            Clip clip = null;

            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);

                if (mixer.isLineSupported(info)) {
                    clip = (Clip) mixer.getLine(info);
                    break;
                }
            }

            if (clip == null) {
                throw new RuntimeException("No compatible audio mixer found for: " + resourcePath);
            }

            clip.open(audioInputStream);

            return clip;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load audio resource: " + resourcePath, e);
        }
    }

    public static void play(String resourcePath) {
        Clip clip = loadClip(resourcePath);
        clip.setFramePosition(0);
        clip.start();
    }
}