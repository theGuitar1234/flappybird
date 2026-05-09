package org.game.engine.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.game.util.constants.AppContext;

public class Sound {
    private static Clip clipFlap;
    private static Clip clipScore;
    private static Clip clipHit;

    private Sound() {}

    public static void init() {
        try {
            File flap = new File(AppContext.FLAP_AUD);
            File hit = new File(AppContext.HIT_AUD);
            File score = new File(AppContext.SCORE_AUD);

            AudioInputStream flapAudioInputStream = AudioSystem.getAudioInputStream(flap);
            AudioInputStream hitAudioInputStream = AudioSystem.getAudioInputStream(hit);
            AudioInputStream scoreAudioInputStream = AudioSystem.getAudioInputStream(score);

            clipFlap = AudioSystem.getClip();
            clipHit = AudioSystem.getClip();
            clipScore = AudioSystem.getClip();

            clipFlap.open(flapAudioInputStream);
            clipHit.open(hitAudioInputStream);
            clipScore.open(scoreAudioInputStream);
        } catch(IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            clipFlap = null;
            clipHit = null;
            clipScore = null;
            e.printStackTrace();
        }
    }

    public static void flap() {
        if (clipFlap == null) return;
        clipFlap.stop();
        clipFlap.setFramePosition(0);
        clipFlap.start();
    }

    public static void stopFlap() {
        if (clipFlap == null) return;
        
        clipFlap.stop();
        clipFlap.setFramePosition(0);
    }

    public static void score() {
        if (clipScore== null) return;
        clipScore.stop();
        clipScore.setFramePosition(0);
        clipScore.start();
    }

    public static void stopScore() {
        if (clipScore == null) return;

        clipScore.stop();
        clipScore.setFramePosition(0);
    }

    public static void close() {
        if (clipFlap != null) {
            clipFlap.close();
            clipFlap = null;
        }
    }

    public static void hit() {
        if (clipHit == null) return;
        clipHit.stop();
        clipHit.setFramePosition(0);
        clipHit.start();
    }

    public static void stopHit() {
        if (clipHit == null) return;
        
        clipHit.stop();
        clipHit.setFramePosition(0);
    }

}
