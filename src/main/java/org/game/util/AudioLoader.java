package org.game.util;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

public final class AudioLoader {

    private static final Map<String, Clip> CLIP_CACHE = new ConcurrentHashMap<>();

    private AudioLoader() {}

    public static void preload(String... resourcePaths) {
        for (String resourcePath : resourcePaths) {
            CLIP_CACHE.computeIfAbsent(resourcePath, AudioLoader::loadClip);
        }
    }

    public static void play(String resourcePath) {
        Clip clip = CLIP_CACHE.computeIfAbsent(resourcePath, AudioLoader::loadClip);

        if (clip.isRunning()) {
            clip.stop();
        }

        clip.setFramePosition(0);
        clip.start();
    }

    private static Clip loadClip(String resourcePath) {
        try {
            URL url = AudioLoader.class.getResource(resourcePath);

            if (url == null) {
                throw new RuntimeException("Missing audio resource: " + resourcePath);
            }

            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url)) {
                AudioFormat format = audioInputStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);

                Clip clip = findCompatibleClip(info);
                clip.open(audioInputStream);

                return clip;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load audio resource: " + resourcePath, e);
        }
    }

    private static Clip findCompatibleClip(DataLine.Info info) throws LineUnavailableException {
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);

            if (mixer.isLineSupported(info)) {
                return (Clip) mixer.getLine(info);
            }
        }

        throw new LineUnavailableException("No compatible audio mixer found for: " + info);
    }
}